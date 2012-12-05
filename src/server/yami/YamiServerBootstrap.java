package yami;

import java.io.*;

import javax.servlet.http.*;

import org.apache.log4j.*;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.*;
import org.eclipse.jetty.servlet.*;

import yami.model.*;

public class YamiServerBootstrap
{
	private final Logger log = Logger.getLogger(YamiServerBootstrap.class);
	
	public static void main(String[] args)
	{
		System.out.println("Starting yami " + YamiVersion.get());
		setLogger(getInstallDir() + "/http-root/" + Constants.SERVER_LOG);
		configureLogLevel();
		new YamiServerBootstrap().runServer();
	}
	
	private static String getInstallDir()
	{
		if (System.getProperty("installDir") != null)
		{
			return System.getProperty("installDir");
		}
		String jarFileString = Constants.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		File jarFile = new File(jarFileString);
		return jarFile.getParentFile().getParent();
	}
	
	private void runServer()
	{
		String installDir = Constants.getInstallDir();
		log.info("Starting yami server at version " + YamiVersion.get());
		int port = Constants.getServerPort();
		log.info("Starting on port " + port + ". To set different server port, use -DserverPort=<port>");
		log.info("starting static server under '/', serving" + installDir + Constants.HTTP_ROOT_CONTEXT);
		ContextHandler staticResouceContextHandler = createStaticContextHandler("/", installDir + Constants.HTTP_ROOT_CONTEXT);
		log.info("starting dashboard servlet under '/dashboard'");
		ServletContextHandler dashboardContext = createServletContext(Constants.DASHBOARD_CONTEXT, new DashboardServlet());
		ContextHandlerCollection contexts = new ContextHandlerCollection();
		contexts.setHandlers(new Handler[] {
				staticResouceContextHandler, dashboardContext
		});
		Server server = new Server(port);
		server.setHandler(contexts);
		try
		{
			new Thread(new UpdaterThread()).start();
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
