package yami;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;

import yami.configuration.Node;
import yami.model.Constants;
import yami.model.DataStore;
import yami.model.DataStoreRetriever;

public class YamiClientBootstrap
{
	private final Logger log = Logger.getLogger(YamiClientBootstrap.class);
	
	public static void main(String[] args)
	{
		try
		{
			setLogger(Constants.getInstallDir() + "/" + Constants.CLIENT_LOG);
			new YamiClientBootstrap().execute();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void execute() throws Exception
	{
		int port = Constants.getClientPort();
		String hostname = java.net.InetAddress.getLocalHost().getHostName();
		log.info("Client will try to start on port " + port + " from directory " + Constants.getInstallDir());
		List<Node> nodes = getNodes(hostname);
		ContextHandlerCollection contexts = createFileServerContexts(nodes, hostname);
		contexts.addHandler(createLogContextHandler());
		for (Node node : nodes)
		{
			log.debug("Starting PeriodicExecuter thread for node " + node.name);
			new Thread(new PeriodicExecuter(20, new RunMonitors(node))).start();
		}
		log.info("Starting server at port " + port);
		Server peerHTTPserver = new Server(port);
		peerHTTPserver.setHandler(contexts);
		peerHTTPserver.start();
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
	private static void setLogger(String logfile) throws IOException
	{
		String pattern = "%d{ISO8601} [%c] %p %m %n";
		PatternLayout layout = new PatternLayout(pattern);
		RollingFileAppender appender = new RollingFileAppender(layout, logfile, true);
		appender.setMaxBackupIndex(5);
		appender.setMaximumFileSize(10 * 1000000);
		Logger.getRootLogger().addAppender(appender);
		Logger.getRootLogger().setLevel(Level.INFO);
		if (System.getProperty("debug") != null && System.getProperty("debug").equals("true"))
		{
			Logger.getRootLogger().setLevel(Level.DEBUG);
		}
	}
	
	// returns a list of nodes to check for this hostname:
	private List<Node> getNodes(String hostname) throws Exception
	{
		boolean found = false;
		DataStore d = DataStoreRetriever.getD();
		List<Node> nodes = new ArrayList<Node>();
		for (Node node : d.appInstances())
		{
			if (!hostname.equals(node.node.name))
			{
				continue;
			}
			found = true;
			nodes.add(node);
		}
		if (!found)
		{
			log.warn("Client " + hostname + " has no monitoring nodes configured");
			throw new Exception("Client is not configured to run on " + hostname);
		}
		return nodes;
	}
	
	// returns a collection of contexts (Context per node)
	private ContextHandlerCollection createFileServerContexts(List<Node> nodes, String hostname)
	{
		ContextHandlerCollection contexts = new ContextHandlerCollection();
		for (Node node : nodes)
		{
			String filesPath = Constants.getInstallDir() + "/nodes/" + node.name;
			createFileSystem(filesPath);
			contexts.addHandler(createStaticContextHandler("/" + node.name, filesPath));
			log.debug(hostname + ":" + node.name + " is served under " + filesPath);
		}
		return contexts;
	}
	
	private ContextHandler createLogContextHandler()
	{
		String logdir = Constants.getInstallDir() + "/log/";
		log.debug("Creating log Context Handler under " + logdir);
		return createStaticContextHandler("/", logdir);
	}
	
	private ContextHandler createStaticContextHandler(String contextPath, String fsPath)
	{
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setWelcomeFiles(new String[] { "index.htm", "index.html" });
		resourceHandler.setResourceBase(fsPath);
		ContextHandler ch = new ContextHandler();
		ch.setContextPath(contextPath);
		ch.setHandler(resourceHandler);
		return ch;
	}
	
}
