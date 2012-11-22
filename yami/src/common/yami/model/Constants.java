package yami.model;

import java.io.File;

import org.apache.log4j.Logger;

public class Constants
{
	private static final Logger log = Logger.getLogger(Constants.class);
	public static final String INSTALLATION = ".";
	public static final String DEFAULT_CLIENT_INSTALL = "/tmp/nbdist.monitoring/";
	public static final int DEFAULT_CLIENT_PORT = 8112;
	public static final int DEFAULT_SERVER_PORT = 8080;
	public static final String DASHBOARD = "/dashboard/";
	public static final String CLIENT_LOG = "log/yami_client.log";
	public static final String SERVER_LOG = "yami_server.log";
	public static final String APP_NAME = "$app_name";
	public static final String NODE_NAME = "$node_name";
	public static final String COLLECTOR_NAME = "$collector_name";
	public static final String CLIENT_LINK = "http://" + NODE_NAME + ":" + getClientPort() + "/" + APP_NAME + "/" + COLLECTOR_NAME + ".txt";
	public static String installDir = null;
	public static String confPath = null;
	
	public static String getConf()
	{
		if (confPath != null)
		{
			return confPath;
		}
		if (System.getProperty("yami.conf") != null)
		{
			return System.getProperty("yami.conf");
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
		if (System.getProperty("installDir") != null)
		{
			return System.getProperty("installDir");
		}
		String jarFileString = Constants.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		File jarFile = new File(jarFileString);
		installDir = jarFile.getParentFile().getParent();
		log.info("SystemProperty 'installDir' not defined, returning default path " + installDir);
		
		return installDir;
	}
	
	public static int getPort(String propertyName, int defaultPort)
	{
		int port = defaultPort;
		if (System.getProperty(propertyName) != null)
		{
			try
			{
				port = Integer.parseInt(System.getProperty(propertyName));
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
				log.warn("got an exception at getPort where propertyName='" + propertyName + "' defaultPort='" + defaultPort + "'", e);
				return defaultPort;
			}
		}
		return port;
	}
	
	public static int getClientPort()
	{
		return getPort("clientPort", DEFAULT_CLIENT_PORT);
	}
	
	public static int getServerPort()
	{
		return getPort("serverPort", DEFAULT_SERVER_PORT);
	}
	
	public static String getServerDashboard()
	{
		String hostname = null;
		try{
			hostname = java.net.InetAddress.getLocalHost().getHostName();
		}catch (Exception e){
			throw new RuntimeException(e);
		}
		return "http://" + hostname  + ":"+ getServerPort()+DASHBOARD;
	}
	
}
