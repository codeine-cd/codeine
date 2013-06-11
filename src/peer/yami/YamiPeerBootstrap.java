package yami;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.DispatcherType;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;

import yami.configuration.ConfigurationManager;
import yami.configuration.Node;
import yami.configuration.Nodes;
import yami.model.Constants;
import yami.model.DataStoreRetriever;
import yami.servlets.InvalidRequestServlet;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;

public class YamiPeerBootstrap
{
	private final Logger log = Logger.getLogger(YamiPeerBootstrap.class);
	private Injector injector;
	
	public static void main(String[] args)
	{
		System.out.println("Starting yami peer " + YamiVersion.get());
		String logfile = Constants.getInstallDir() + "/" + Constants.CLIENT_LOG;
		System.out.println("writing log to " + logfile);
		setLogger(logfile);
		try
		{
			new YamiPeerBootstrap().execute();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void execute() throws Exception
	{
		log.info("Starting yami peer at version " + YamiVersion.get());
		injector = Guice.createInjector(new YamiPeerModule(), new YamiPeerServletModule(), new AbstractModule() {
		    @Override
		    protected void configure() {
		        binder().requireExplicitBindings();
		        bind(GuiceFilter.class);
		    }
		});
		FilterHolder guiceFilter = new FilterHolder(injector.getInstance(GuiceFilter.class));
		ServletContextHandler handler = new ServletContextHandler();
		handler.setContextPath("/");
		handler.addServlet(InvalidRequestServlet.class, "/*");
		handler.addFilter(guiceFilter, "/*", EnumSet.allOf(DispatcherType.class));
		
		ConfigurationManager cm = injector.getInstance(ConfigurationManager.class);
		int port = cm.getCurrentGlobalConfiguration().getPeerPort();
		String hostname = java.net.InetAddress.getLocalHost().getHostName();
		log.info("Hostname " + hostname);
		log.info("Peer will try to start on port " + port + " from directory " + Constants.getInstallDir());
		List<Node> nodes = Nodes.getNodes(hostname, DataStoreRetriever.getD());
		Node internalNode = nodes.get(0).peer.internalNode();//new Node("yami_internal_node", "yami_internal_node", );
		startInternalNodeMonitoring(internalNode );
		startNodeMonitoringThreads(nodes);
		ArrayList<Node> nodesWithInternalNode = Lists.newArrayList(nodes);
		nodesWithInternalNode.add(internalNode);
		
		ContextHandlerCollection contexts = createFileServerContexts(nodesWithInternalNode, hostname);
		contexts.addHandler(handler);
		Server peerHTTPserver = injector.getInstance(Server.class);
		peerHTTPserver.setHandler(contexts);
		peerHTTPserver.start();
		peerHTTPserver.join();
		while (true)
		{
			log.info("HTTP server is stopped. Sleeping for 20 seconds");
			Thread.sleep(TimeUnit.SECONDS.toMillis(20));
		}
	}
	
	private void startInternalNodeMonitoring(Node node) {
	    new Thread(new PeriodicExecuter(1, new RunInternalMonitors(node, injector.getInstance(ConfigurationManager.class)))).start();
	}

	private void startNodeMonitoringThreads(List<Node> nodes)
	{
		for (Node node : nodes)
		{
			if (node.disabled())
			{
				log.info("node is disabled " + node.name);
				continue;
			}
			log.info("Starting PeriodicExecuter thread for node " + node.name);
			new Thread(new PeriodicExecuter(1, new RunMonitors(node, injector.getInstance(ConfigurationManager.class)))).start();
		}
	}
	
	// create the directory structure under "path" if does not already exists:
	private void createFileSystem(String path)
	{
		log.debug("Will try to create directory structure " + path);
		File f = new File(path);
		if (f.exists())
		{
			return;
		}
		if (f.mkdirs() == false)
		{
			log.fatal("Failed to create directory structure " + path);
			System.exit(2);
		}
	}
	
	// logger initialization:
	private static void setLogger(String logfile)
	{
		String pattern = "%d{ISO8601} [%c] %p %m %n";
		PatternLayout layout = new PatternLayout(pattern);
		RollingFileAppender appender;
		try {
			appender = new RollingFileAppender(layout, logfile, true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		appender.setMaxBackupIndex(5);
		appender.setMaximumFileSize(10 * 1000000);
		Logger.getRootLogger().addAppender(appender);
		Logger.getRootLogger().setLevel(Level.INFO);
		if (System.getProperty("debug") != null && System.getProperty("debug").equals("true"))
		{
			Logger.getRootLogger().setLevel(Level.DEBUG);
		}
	}
	
	// returns a collection of contexts (Context per node)
	private ContextHandlerCollection createFileServerContexts(List<Node> nodes, String hostname)
	{
		ContextHandlerCollection contexts = new ContextHandlerCollection();
		for (Node node : nodes)
		{
			String filesPath = Constants.getInstallDir() + Constants.NODES_DIR + node.name;
			createFileSystem(filesPath);
			contexts.addHandler(createStaticContextHandler("/" + node.name, filesPath));
			log.debug(hostname + ":" + node.name + " is served under " + filesPath);
		}
		contexts.addHandler(createLogContextHandler());
		return contexts;
	}
	
	private ContextHandler createLogContextHandler()
	{
		String logdir = Constants.getInstallDir() + Constants.LOG_DIR;
		log.debug("Creating log Context Handler under " + logdir);
		return createStaticContextHandler(Constants.RESOURCESS_CONTEXT, logdir);
	}
	
	private ContextHandler createStaticContextHandler(String contextPath, String fsPath)
	{
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setWelcomeFiles(new String[] {});
		resourceHandler.setResourceBase(fsPath);
		ContextHandler ch = new ContextHandler();
		ch.setContextPath(contextPath);
		ch.setHandler(resourceHandler);
		return ch;
	}
	
}
