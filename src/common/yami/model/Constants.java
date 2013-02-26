package yami.model;

import java.io.File;

import org.apache.log4j.Logger;

import yami.configuration.ConfigurationManager;

public class Constants
{
	private static final Logger log = Logger.getLogger(Constants.class);
	
	public static final String DEFAULT_CLIENT_INSTALL = "/tmp/yami.monitoring/";
	public static final int DEFAULT_CLIENT_PORT = 8112;
	public static final int DEFAULT_SERVER_PORT = 8080;
	
	public static final String CLIENT_LOG = "log/yami_client.log";
	public static final String SERVER_LOG = "yami_server.log";
	
	public static final String APP_NAME = "$app_name";
	public static final String NODE_NAME = "$node_name";
	public static final String COLLECTOR_NAME = "$collector_name";
	public static final String CLIENT_PORT = "$client_port";
	public static final String CLIENT_LINK = "http://" + NODE_NAME + ":" + CLIENT_PORT + "/" + APP_NAME + "/" + COLLECTOR_NAME + ".txt";
	
	public static final String INSTALLATION = ".";
	public static final String NODES_DIR = "/nodes/";
	public static final String MONITORS_DIR = "/monitors/";
	public static final String LOG_DIR = "/log/";
	
	public static final String DASHBOARD_CONTEXT = "/dashboard";
	public static final String PEERS_DASHBOARD_CONTEXT = "/peers";
	public static final String RESTART_CONTEXT = "/restart";
	public static final String HTTP_ROOT_CONTEXT = "/http-root";
	
	public static String installDir = null;
	public static String confPath = null;
	
	public static String getConfPath()
	{
		if (confPath != null)
		{
			return confPath;
		}
		if (System.getProperty("conf_file") != null)
		{
			confPath = System.getProperty("conf_file");
			if (!confPath.startsWith("/"))
			{
				confPath = getInstallDir() + "/conf/" + confPath;
			}
			return confPath;
		}
		confPath = getInstallDir() + "/conf/yami.conf.xml";
		log.info("SystemProperty 'yami.conf' not defined, returning default path " + confPath);
		return confPath;
	}
	
	public static String getInstallDir()
	{
		if (installDir != null)
		{
			return installDir;
		}
		if (System.getProperty("install.dir") != null)
		{
			log.debug("setting installDir from properties: " + System.getProperty("install.dir"));
			installDir = System.getProperty("install.dir");
			return installDir;
		}
		// if install dir not set, get relative to cwd:
		String sJarPath = Constants.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		File jarFile = new File(sJarPath);
		installDir = jarFile.getParentFile().getParent();
		log.info("SystemProperty 'install.dir' not defined, setting installDir as '" + installDir + "'");
		return installDir;
	}
	
	// public static int getPort(String propertyName, int defaultPort)
	// {
	// int port = defaultPort;
	// if (System.getProperty(propertyName) != null)
	// {
	// try
	// {
	// port = Integer.parseInt(System.getProperty(propertyName));
	// }
	// catch (NumberFormatException e)
	// {
	// log.warn("got an exception at getPort where propertyName='" + propertyName + "' defaultPort='" + defaultPort +
	// "'", e);
	// return defaultPort;
	// }
	// }
	// return port;
	// }
	
	// public static int getClientPort()
	// {
	// return getPort("client.port", DEFAULT_CLIENT_PORT);
	// }
	
	// public static int getServerPort()
	// {
	// return getPort("server.port", DEFAULT_SERVER_PORT);
	// }
	
	public static String getServerDashboard()
	{
		String hostname = null;
		try
		{
			hostname = java.net.InetAddress.getLocalHost().getHostName();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		return "http://" + hostname + ":" + ConfigurationManager.getInstance().getCurrentGlobalConfiguration().getServerPort() + DASHBOARD_CONTEXT;
	}
	
}
