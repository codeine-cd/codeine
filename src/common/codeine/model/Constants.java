package codeine.model;

import codeine.utils.StringUtils;
import codeine.utils.network.InetUtils;

import java.io.File;

public class Constants {


    public static final boolean SECURITY_ENABLED = true;

    public static final String HTTP_ROOT_CONTEXT = "/http-root";
    public static final String GUEST_USER = "Guest";

    public static final String CODEINE_NODES_PROJECT_NAME = "Codeine_Internal_Nodes_Project";

    public static final String PEER_LOG = "codeine_peer.log";
    public static final String DIRECTORY_LOG = "codeine_directory.log";
    public static final String SERVER_LOG = "codeine_server.log";
    public static final String PROJECT_CONF_FILE = "project.conf.json";
    public static final String INSTALLATION = ".";
    public static final String MONITORS_DIR = "/monitors";
    public static final String CONF_DIR = "/conf";
    public static final String COMMANDS_DIR = "/plugins";
    public static final String COMMANDS_OUTPUT_DIR = "/plugins_output";
    public static final String ROOT_CONTEXT = "/";
    public static final String ANGULAR_CONTEXT = "/codeine";
    public static final String API_CONTEXT = "/api";
    public static final String API_WITH_TOKEN_CONTEXT = "/api-with-token";


    public static final String MONITOR_OUTPUT_CONTEXT = "/monitor-output";
    public static final String COLLECTOR_OUTPUT_CONTEXT = "/collector-output";
    public static final String PROJECT_FILES_CONTEXT = "/projects-files";

    public static final String PROJECT_NODES_CONTEXT = "/project-nodes";
    public static final String PROJECT_NODES_ALIASES_CONTEXT = "/project-nodes-aliases";
    public static final String PROJECT_NODES_CSV_CONTEXT = "/csv-nodes";
    public static final String PROJECT_STATUS_CONTEXT = "/project-status";
    public static final String NODE_STATUS_CONTEXT = "/node-status";
    public static final String MONITOR_STATUS_CONTEXT = "/monitor-status";
    public static final String COLLECTOR_STATUS_CONTEXT = "/collector-status";
    public static final String COMMAND_STATUS_CONTEXT = "/command-status";
    public static final String COMMAND_OUTPUT_CONTEXT = "/command-output";
    public static final String REPORTER_CONTEXT = "/reporter";
    public static final String METRICS_CONTEXT = "/metrics";
    public static final String HEALTH_CONTEXT = "/health";
    public static final String UPGRADE_SERVER_CONTEXT = "/upgrade-server";
    public static final String COMMANDS_LOG_CONTEXT = "/commands-log";
    public static final String COMMANDS_EXECUTER_STATUS = "/commands-status";
    public static final String RESTART_ALL_PEERS_CONTEXT = "/restart-all";
    public static final String PROJECTS_LIST_CONTEXT = "/projects";
    public static final String PROJECT_PATH = "/project";
    public static final String NODE_PATH = "/node";
    public static final String COMMAND_NODE_CONTEXT = "/command-node";
    public static final String RELOAD_CONFIGURATION_CONTEXT = "/reload-configuration";
    public static final String REGISTER_PEER_IN_DIRECTORY_CONTEXT = "/register-in-directory";
    public static final String COMMAND_NODE_ALL_CONTEXT = "/command-node-all";
    public static final String COMMAND_NODES_CONTEXT = "/command-nodes";
    public static final String RESOURCESS_CONTEXT = "/resources";
    public static final String CONFIG_SUBMIT_CONTEXT = "/config-submit";
    public static final String PEER_STATUS_CONTEXT = "/peer/status";
    public static final String PEER_PROJECT_STATUS_CONTEXT = "/project/status";

    public static final String MONITORS_STATISTICS_CONTEXT = "/monitors-statistics";

    public static final String INFO_CONTEXT = "/info";
    public static final String USER_INFO_CONTEXT = "/user-info";
    public static final String MANAGE_STATISTICS_INFO_CONTEXT = "/manage-statistics";
    public static final String VERSION_INFO_CONTEXT = INFO_CONTEXT + "/version";
    public static final String VERSIONS_MAPPING_CONTEXT = "/versions-mapping";
    public static final String PEER_REPORT_CONTEXT = "/peer-report";
    public static final String RSYNC_SOURCE = "/version/rsync";
    public static final String COMMAND_NODE_IN_SERVER_CONTEXT = "/command-to-node";
    public static final String PEER_PORT_CONTEXT = "/peer/port";
    public static final String REGISTER_CONTEXT = "/register";
    public static final String PREPARE_FOR_SHUTDOWN_CONTEXT = "/prepare-for-shutdown";
    public static final String CANCEL_SHUTDOWN_CONTEXT = "/cancel-shutdown";
    public static final String GLOBAL_CONFIGURATION_CONTEXT = "/global-configuration";
    public static final String EXPERIMENTAL_CONFIGURATION_CONTEXT = "/experimental-configuration";
    public static final String PROJECT_CONFIGURATION_CONTEXT = "/project-configuration";
    public static final String PROJECT_COMMANDS_CONFIGURATION_CONTEXT = "/project-commands-configuration";
    public static final String PERMISSIONS_CONFIGURATION_CONTEXT = "/permissions";
    public static final String SESSION_INFO_CONTEXT = "/session-info";
    public static final String PUSH_PROJECTS_TO_DB_CONTEXT = "/push-projects-to-db";

    public static final String REPLACE_NODE_NAME = "$node_name";
    public static final String DB_NAME = "codeineDB";
    public static final int DB_PORT = 27017;

    public static final boolean IS_MAIL_STARTEGY_MONGO = true;

    public static final String NO_VERSION = "No version";
    public static final String ALL_VERSION = "All versions";
    public static final String VERSION = "version";
    public static final String VERSION_COLLECTOR_NAME = "node_version_collector";
    public static final String TAGS_COLLECTOR_NAME = "node_tags_collector";

    public static final String COMMAND_RESULT = "codeine-command-result=";

    public static final String JSON_COMMAND_FILE_NAME = "/command.json";

    public static final String EXECUTION_ENV_CONFIGURATION_STEP = "CODEINE_CONFIGURATION_STEP";
    public static final String EXECUTION_ENV_OUTPUT_FILE = "CODEINE_OUTPUT_FILE";
    public static final String EXECUTION_ENV_PROJECT_NAME = "CODEINE_PROJECT_NAME";
    public static final String EXECUTION_ENV_USER_NAME = "CODEINE_USER_NAME";
    public static final String EXECUTION_ENV_PROJECT_STATUS = "CODEINE_PROJECT_STATUS";
    public static final String EXECUTION_ENV_NODE_NAME = "CODEINE_NODE_NAME";
    public static final String EXECUTION_ENV_NODE_ALIAS = "CODEINE_NODE_ALIAS";
    public static final String EXECUTION_ENV_NODE_TAGS = "CODEINE_NODE_TAGS";
    public static final String EXECUTION_ENV_CODEINE_SERVER = "CODEINE_HOST";
    public static final String EXECUTION_ENV_CODEINE_SERVER_PORT = "CODEINE_PORT";
    public static final String ENV_CODEINE_WORKAREA = "CODEINE_WORKAREA";
    public static final String ENV_CODEINE_HOST_WORKAREA = "CODEINE_HOST_WORKAREA";

    public static String installDir = null;

    public static String getConfDir() {
        return getWorkarea() + "/conf";
    }

    public static String getConfPath() {
        return getConfDir() + "/codeine.conf.json";
    }

    public static String getViewConfPath() {
        return getConfDir() + "/codeine.view.conf.json";
    }

    public static String getExperimentalConfPath() {
        return getConfDir() + "/codeine.experimental.conf.json";
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

    public static String getLogDir() {
        return System.getProperty("log.dir", getResourcesDir());
    }

    public static String getResourcesDir() {
        return Constants.getInstallDir() + HTTP_ROOT_CONTEXT;
    }

    public static String getAngularDir() {
        String appDir = System.getProperty("angularAppDir");
        if (StringUtils.isEmpty(appDir)) {
            appDir = "dist";
        }
        return Constants.getResourcesDir() + "/ajs/" + appDir;
    }

    public static String getAngularMainHtml() {
        return Constants.getAngularDir() + "/index.html";
    }

    public static String getInstallDir() {
        if (installDir != null) {
            return installDir;
        }
        installDir = System.getProperty("installDir");
        if (installDir == null) {
            // if install dir not set, get relative to cwd:
            String sJarPath = Constants.class.getProtectionDomain().getCodeSource().getLocation()
                .getPath();
            File jarFile = new File(sJarPath);
            installDir = jarFile.getParentFile().getParent();
        }
        System.out.println("Setting installDir to '" + installDir + "'"); //$NON-NLS-2$
        return installDir;
    }

    public static final String COMMAND_FINISH_FILE = "/finished";

    public static final String COMMAND_LOG_FILE = "/log";

    public static final int DEFAULT_WEB_SERVER_PORT = 12347;
    public static final int DEFAULT_PEER_PORT = 49671;

    public static final String DISCOVERY_PLUGIN = "discovery";

    public static final String API_TOKEN = "api_token";

    public static final String PROJECT_TAGS_CONTEXT = "/project-tags";

    public static final String ANGULAR_WEB_URLS_PATH_SPEC = "/codeine/*";

    public static final String ANGULAR_RESOURCES_CONTEXT_PATH = "/ajs";

    public static final String PROJECTS_TABS_CONTEXT = "/projects-tabs";


    public static class RequestHeaders {

        public static final String NO_ZIP = "NO_ZIP";

    }

    public static class UrlParameters {

        public static final String PROJECT_NAME = "project";
        public static final String VERSION_NAME = "version";
        public static final String NODE_NAME = "node-name";
        public static final String NODE = "node";
        public static final String MONITOR = "monitor";
        public static final String COLLECTOR = "collector";
        public static final String COMMAND_NAME = "command";
        public static final String PATH_NAME = "path";
        public static final String RESOURCE_NAME = "resource";
        public static final String LINK_NAME = "link";
        public static final String DATA_NAME = "data";
        public static final String REDIRECT = "redirect";
        public static final String COMMAND_ID = "command-id";
        public static final String DATA_ADDITIONAL_COMMAND_INFO_NAME = "more-command-info";
        public static final String SECTION = "section";
        public static final String VIEW_AS = "viewas";
        public static final String FILTER = "text-filter";
        public static final String FORCE = "force";
        public static final String ADDRESS = "server";
        public static final String INCLUDE_OUTPUT = "output";
        public static final String SYNC_REQUEST = "sync";
    }

    public static String getPersistentDir() {
        return getWorkarea() + "/persistent." + InetUtils.getLocalHost().getHostName();
    }

    public static String getWorkarea() {
        String workarea = System.getenv(ENV_CODEINE_WORKAREA);
        if (null == workarea) {
            return getInstallDir();
        }
        return workarea;
    }

    public static String getHostWorkareaDir() {
//		return Constants.getInstallDir() + "/../..";
        String workarea = System.getenv(ENV_CODEINE_HOST_WORKAREA);
        if (null == workarea) {
            return getPersistentDir();//assuming workarea is common somehow, so pid+port files will be separate for each host
        }
        return workarea;
    }

    public static String apiContext(String projectNodesContext) {
        return API_CONTEXT + projectNodesContext;
    }

    public static String apiTokenContext(String projectNodesContext) {
        return API_WITH_TOKEN_CONTEXT + projectNodesContext;
    }

    public static final String OFFLINE_NODES = "Codeine daemon not running";
    public static final String NOT_REPORTING_NODES = "Codeine daemon not reporting node";

    public static final String CODEINE_API_TOKEN_DERIVER = "CodeineSecretKey";


    public static String getFeatureFlagsDir() {
        return getWorkarea() + "/features";
    }
}
