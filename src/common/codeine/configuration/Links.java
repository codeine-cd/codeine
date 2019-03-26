package codeine.configuration;

import javax.inject.Inject;

import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.utils.network.HttpUtils;

public class Links {

    private @Inject
    GlobalConfigurationJsonStore globalConfiguration;

    public String directoryPeerStatus() {
        return "http://" + globalConfiguration.get().directory_host() + ":" + globalConfiguration.get().directory_port()
            + Constants.PEER_STATUS_CONTEXT;
    }

    public String getLogLink(String hostport) {
        return "http://" + hostport + Constants.RESOURCESS_CONTEXT;
    }

    public String getPeerLink(String hostport) {
        return "http://" + hostport;
    }

    public String getPeerCommandLink(String hostport, String project, String command, String userArgs) {
        String args = null == userArgs ? "" : "&version=" + HttpUtils.encodeURL(userArgs);
        return getPeerLink(hostport) + Constants.COMMAND_NODE_CONTEXT + "?project=" + HttpUtils.encodeURL(project)
            + "&command=" + HttpUtils.encodeURL(command) + args;
    }

    public String getProjectLink(String name) {
        return Constants.PROJECT_STATUS_CONTEXT + "?project=" + HttpUtils.encodeURL(name);
    }

    public String getPeerMonitorResultLink(String hostport, String projectName, String collectorName, String nodeName) {
        String nodeContextPath = getNodeMonitorOutputContextPath(projectName);
        return getPeerLink(hostport) + nodeContextPath + "/" + HttpUtils.specialEncode(nodeName) + "/" + HttpUtils
            .specialEncode(collectorName) + ".txt";
    }

    public String getPeerCollectorResultLink(String hostport, String projectName, String collectorName,
        String nodeName) {
        String nodeContextPath = getNodeCollectorOutputContextPath(projectName);
        return getPeerLink(hostport) + nodeContextPath + "/" + HttpUtils.specialEncode(nodeName) + "/" + HttpUtils
            .specialEncode(collectorName) + ".txt";
    }

    public String getWebServerLink() {
        return "http://" + globalConfiguration.get().web_server_host() + ":" + globalConfiguration.get()
            .web_server_port();
    }

    public String getNodeMonitorOutputContextPath(String projectName) {
        return getNodeMonitorOutputContextPathAllProjects() + "/" + HttpUtils.encodeURL(projectName)
            + Constants.MONITOR_OUTPUT_CONTEXT + Constants.NODE_PATH;
    }

    public String getNodeCollectorOutputContextPath(String projectName) {
        return getNodeMonitorOutputContextPathAllProjects() + "/" + HttpUtils.encodeURL(projectName)
            + Constants.COLLECTOR_OUTPUT_CONTEXT + Constants.NODE_PATH;
    }

    public String getNodeMonitorOutputContextPathAllProjects() {
        return Constants.PROJECT_PATH;
    }

    public String getWebServerLandingPage() {
        return getWebServerLink() + Constants.PROJECTS_LIST_CONTEXT;
    }

    public String getWebServerProjectAlerts(ProjectJson project) {
        return getWebServerLink() + "/codeine/project/" + HttpUtils.encodeURL(project.name()) + "/status";
    }

    public String getWebServerCollectorStatus(String project_name, String node_name, String collector_name) {
        return getWebServerLink() + "/codeine/project/" + HttpUtils.encodeURL(project_name) + "/node/" + HttpUtils
            .encodeURL(node_name) + "/collector/" + HttpUtils.encodeURL(collector_name) + "/status";
    }

    public String getWebServerCommandStatus(String project_name, String command_name, long command_id) {
        return getWebServerLink() + "/codeine/project/" + HttpUtils.encodeURL(project_name) + "/command/" + HttpUtils
            .encodeURL(command_name) + "/" + command_id + "/status";
    }

}
