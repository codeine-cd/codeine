package codeine.model;

import java.io.File;

public class Constants
{
	public static final boolean SECURITY_ENABLED = true;

	public static final String HTTP_ROOT_CONTEXT = "/http-root";
	
	public static final String PEER_LOG = "codeine_peer.log";
	public static final String DIRECTORY_LOG = "codeine_directory.log";
	public static final String SERVER_LOG = "codeine_server.log";
	
	public static final String INSTALLATION = ".";
	public static final String MONITORS_DIR = "/monitors";
	public static final String CONF_DIR = "/conf";
	public static final String PLUGINS_DIR = "/plugins";
	public static final String PLUGINS_OUTPUT_DIR = "/plugins_output";
	public static final String MONITOR_OUTPUT_CONTEXT = "/monitor-output";
	public static final String RAW_OUTPUT_CONTEXT = "/raw-output";
	public static final String PROGRESSIVE_RAW_OUTPUT_CONTEXT = "/progressive-raw-output";
	public static final String FILE_GETTER_CONTEXT = "/file-getter";
	public static final String PROJECT_FILES_CONTEXT = "/projects-files";
	
	public static final String DASHBOARD_CONTEXT = "/dashboard";
	public static final String AGGREGATE_NODE_CONTEXT = "/aggregate-node";
	public static final String COMMANDS_LOG_CONTEXT = "/commands-log";
	public static final String COMMANDS_EXECUTER_STATUS = "/commands-status";
	public static final String RESTART_ALL_PEERS_CONTEXT = "/restart-all";
	public static final String PROJECTS_DASHBOARD_CONTEXT = "/projects";
	public static final String MANAGEMENT_CONTEXT = "/manage";
	public static final String PROJECT_PATH = "/project";
	public static final String NODE_PATH = "/node";
	public static final String COMMAND_NODE_CONTEXT = "/command-node";
	public static final String REGISTER_PEER_IN_DIRECTORY_CONTEXT = "/register-in-directory";
	public static final String COMMAND_NODE_ALL_CONTEXT = "/command-node-all";
	public static final String COMMAND_NODES_CONTEXT = "/command-nodes";
	public static final String SCHEDULE_COMMAND_CONTEXT = "/schedule-command";
	public static final String LABELS_CONTEXT = "/labels";
	public static final String LABEL_CONTEXT = "/label-version";
	public static final String RESOURCESS_CONTEXT = "/resources";
	public static final String CONFIGURE_CONTEXT = "/configure";
	public static final String CONFIG_SUBMIT_CONTEXT = "/config-submit";
	public static final String PEER_STATUS_CONTEXT = "/peer/status";
	public static final String PROJECT_STATUS_CONTEXT = "/project/status";
	public static final String PROJECT_CONF_FILE = "project.conf.json";
	public static final String NODES_CONF_FILE = "nodes.conf.json";

	public static final String INFO_CONTEXT = "/info";
	public static final String VERSION_INFO_CONTEXT = INFO_CONTEXT + "/version";
	public static final String VERSIONS_MAPPING_CONTEXT = "/versions-mapping";
	public static final String RSYNC_SOURCE = "/version/rsync";
	public static final String COMMAND_NODE_IN_SERVER_CONTEXT = "/command-to-node";
	public static final String PEER_PORT_CONTEXT = "/peer/port";
	public static final String REGISTER_CONTEXT = "/register";

	public static final String REPLACE_NODE_NAME = "$node_name";	
	public static final String DB_NAME = "codeineDB";
	public static final int DB_PORT = 27017;
	
	public static final boolean IS_MAIL_STARTEGY_MONGO = true;
	
	public static final String NO_VERSION = "No version";
	public static final String ALL_VERSION = "All versions";
	public static final String VERSION = "version";

	public static final int ERROR_MONITOR = -1;

	public static final String COMMAND_RESULT = "codeine-command-result=";

	public static final String JSON_COMMAND_FILE_NAME = "/command.json";
	
	public static String installDir = null;
	
	public static String getConfDir() {
		return getInstallDir() + "/conf";
	}
	public static String getConfPath() {
		return getInstallDir() + "/conf/codeine.conf.json";
	}
	public static String getIdentityConfPath() {
		return getConfDir() + "/identity.json";
	}
	public static String getPermissionsConfPath() {
		return getConfDir() + "/permissions.json";
	}
	public static String getSpnegoPropertiesPath() {
		return getConfDir() + "/spnego.properties";
	}
	
	public static String getLogDir(){
		return System.getProperty("log.dir", getResourcesDir());
	}
	public static String getResourcesDir() {
		return Constants.getInstallDir() + HTTP_ROOT_CONTEXT;
	}
	
	public static String getInstallDir()
	{
		if (installDir != null)
		{
			return installDir;
		}
		installDir = System.getProperty("installDir");
		if (installDir == null)
		{
			// if install dir not set, get relative to cwd:
			String sJarPath = Constants.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			File jarFile = new File(sJarPath);
			installDir = jarFile.getParentFile().getParent();
		}
		System.out.println("Setting installDir to '" + installDir + "'");
		return installDir;
	}

	public static final String COMMAND_FINISH_FILE = "/finished";

	public static final String COMMAND_LOG_FILE = "/log";
	
	public static class UrlParameters {
		public static final String PROJECT_NAME = "project";
		public static final String VERSION_NAME = "version";
		public static final String COMMAND_NAME = "command";
		public static final String PATH_NAME = "path";
		public static final String RESOURCE_NAME = "resource";
		public static final String LINK_NAME = "link";
		public static final String DATA_NAME = "data";
		public static final String COMMAND_ID = "command-id";
		
		
	}

}
