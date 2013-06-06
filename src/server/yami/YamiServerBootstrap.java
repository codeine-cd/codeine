package yami;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import yami.configuration.ConfigurationManager;
import yami.model.Constants;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;

public class YamiServerBootstrap
{
	private final Logger log = Logger.getLogger(YamiServerBootstrap.class);
	private Injector injector;
	
	public static void main(String[] args)
	{
		System.out.println("Starting yami server " + YamiVersion.get());
		String installDir = Constants.getInstallDir();
		setLogger(installDir + "/http-root/" + Constants.SERVER_LOG);
		configureLogLevel();
		new YamiServerBootstrap().runServer();
	}
	
	private void runServer()
	{
		injector = Guice.createInjector(new YamiServerModule(), new YamiServerServletModule(), new AbstractModule() {
		    @Override
		    protected void configure() {
//		        binder().requireExplicitBindings();
		        bind(GuiceFilter.class);
		    }
		});

		FilterHolder guiceFilter = new FilterHolder(injector.getInstance(GuiceFilter.class));
		ServletContextHandler handler = new ServletContextHandler();
		handler.setContextPath("/");
		handler.addFilter(guiceFilter, "/*", EnumSet.allOf(DispatcherType.class));
		ConfigurationManager cm = injector.getInstance(ConfigurationManager.class);
		String installDir = Constants.getInstallDir();
		log.info("Starting yami server at version " + YamiVersion.get());
		int port = cm.getCurrentGlobalConfiguration().getServerPort();
		log.info("Starting on port " + port + ". To set different server port, use -Dserver.port=<port>");
		log.info("starting static server under '/', serving" + installDir + Constants.HTTP_ROOT_CONTEXT);
		ContextHandler staticResouceContextHandler = createStaticContextHandler("/resource", installDir + Constants.HTTP_ROOT_CONTEXT);
		log.info("starting dashboard servlet under '/dashboard'");
		ContextHandlerCollection contexts = new ContextHandlerCollection();
		contexts.setHandlers(new Handler[] {
				staticResouceContextHandler, handler,
		});
		Server server = new Server(port);
		server.setHandler(contexts);
		try
		{
			new Thread(new UpdaterThread(new YamiMailSender(injector.getInstance(SendMailStrategy.class)), injector.getInstance(CollectorHttpResultFetcher.class), true)).start();
			server.start();
		}
		catch (Exception e)
		{
			log.error("got an exception at server main ", e);
			e.printStackTrace();
		}
	}
	
	private static void setLogger(String logfile) throws RuntimeException
	{
		if (Boolean.getBoolean("log.to.stdout"))
		{
			System.out.println("logging to std-out");
			BasicConfigurator.configure();
			Logger.getRootLogger().setLevel(Level.INFO);
			return;
		}
		String pattern = "%d{ISO8601} [%c] %p %m %n";
		PatternLayout layout = new PatternLayout(pattern);
		
		RollingFileAppender appender = null;
		try
		{
			appender = new RollingFileAppender(layout, logfile, true);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		appender.setMaxBackupIndex(5);
		appender.setMaximumFileSize(10 * 1000000);
		Logger.getRootLogger().addAppender(appender);
		Logger.getRootLogger().setLevel(Level.INFO);
	}
	
	private static void configureLogLevel()
	{
		if (System.getProperty("debug") != null && System.getProperty("debug").equals("true"))
		{
			Logger.getRootLogger().setLevel(Level.DEBUG);
		}
	}
	
	private static ContextHandler createStaticContextHandler(String contextPath, String fsPath)
	{
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setWelcomeFiles(new String[] {
				"index.htm", "index.html"
		});
		resourceHandler.setResourceBase(fsPath);
		ContextHandler ch = new ContextHandler();
		ch.setContextPath(contextPath);
		ch.setHandler(resourceHandler);
		return ch;
	}
	
	private ServletContextHandler createServletContext(String context, HttpServlet servlet)
	{
		ServletContextHandler monitorContext = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
		monitorContext.setContextPath(context);
		monitorContext.addServlet(new ServletHolder(servlet), "/");
		return monitorContext;
	}
}
