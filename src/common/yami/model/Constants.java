package yami.model;

import java.io.File;

public class Constants
{
	public static final String DEFAULT_CLIENT_INSTALL = "/tmp/yami.monitoring/";
	public static final int DEFAULT_CLIENT_PORT = 8112;
	public static final int DEFAULT_SERVER_PORT = 8080;
	
	public static final String CLIENT_LOG = "log/yami_client.log";
	public static final String SERVER_LOG = "yami_server.log";
	
	public static final String NODE_NAME = "$app_name";
	public static final String PEER_NAME = "$node_name";
	public static final String COLLECTOR_NAME = "$collector_name";
	public static final String CLIENT_PORT = "$client_port";
	public static final String CLIENT_LINK = "http://" + PEER_NAME + ":" + CLIENT_PORT + "/" + NODE_NAME + "/" + COLLECTOR_NAME + ".txt";
	
	public static final String INSTALLATION = ".";
	public static final String NODES_DIR = "/nodes/";
	public static final String MONITORS_DIR = "/monitors/";
	public static final String LOG_DIR = "/log/";
	
	public static final String DASHBOARD_CONTEXT = "/dashboard";
	public static final String AGGREGATE_NODE_CONTEXT = "/aggregate-node";
	public static final String RESTART_ALL_PEERS_CONTEXT = "/restart-all";
	public static final String PEERS_DASHBOARD_CONTEXT = "/peers";
	public static final String RESTART_CONTEXT = "/restart";
	public static final String COMMAND_NODE_CONTEXT = "/command-node";
	public static final String COMMAND_NODE_ALL_CONTEXT = "/command-node-all";
	public static final String HTTP_ROOT_CONTEXT = "/http-root";
	public static final String RESOURCESS_CONTEXT = "/resources";
	
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
		System.out.println("SystemProperty 'yami.conf' not defined, returning default path " + confPath);
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
			System.out.println("setting installDir from properties: " + System.getProperty("install.dir"));
			installDir = System.getProperty("install.dir");
			return installDir;
		}
		// if install dir not set, get relative to cwd:
		String sJarPath = Constants.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		File jarFile = new File(sJarPath);
		installDir = jarFile.getParentFile().getParent();
		System.out.println("SystemProperty 'install.dir' not defined, setting installDir as '" + installDir + "'");
		return installDir;
	}
	
}
