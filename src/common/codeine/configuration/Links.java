package codeine.configuration;

import javax.inject.Inject;

import codeine.jsons.CommandExecutionStatusInfo;
import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.utils.network.HttpUtils;

public class Links {

	private @Inject	GlobalConfigurationJsonStore globalConfiguration;

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
		String args = null == userArgs ? "" : "&version=" + HttpUtils.encode(userArgs);
		return getPeerLink(hostport) + Constants.COMMAND_NODE_CONTEXT + "?project=" + HttpUtils.encode(project) + "&command=" + HttpUtils.encode(command) + args;
	}

	public String getProjectLink(String name) {
		return Constants.PROJECT_STATUS_CONTEXT + "?project="+HttpUtils.encode(name);
	}

	public String getPeerMonitorResultLink(String hostport, String projectName, String collectorName, String nodeName) {
		String nodeContextPath = getNodeMonitorOutputContextPath(projectName);
		return getPeerLink(hostport) + nodeContextPath + "/" + HttpUtils.specialEncode(nodeName) + "/" + HttpUtils.specialEncode(collectorName) + ".txt";
	}

	public String getWebServerProjectAlerts(ProjectJson project)
	{
		return getWebServerLink() + Constants.PROJECT_NODES_CONTEXT + "?alerts=true&project=" + HttpUtils.encode(project.name());
	}

	public String getWebServerLink() {
		return "http://" + globalConfiguration.get().web_server_host() + ":" + globalConfiguration.get().web_server_port();
	}

	public String getNodeMonitorOutputContextPath(String projectName) {
		return getNodeMonitorOutputContextPathAllProjects() + "/" + HttpUtils.encode(projectName) + Constants.MONITOR_OUTPUT_CONTEXT + Constants.NODE_PATH;
	}
	public String getNodeMonitorOutputContextPathAllProjects() {
		return Constants.PROJECT_PATH;
	}

	public String getWebServerLandingPage() {
		return getWebServerLink() + Constants.PROJECTS_LIST_CONTEXT;
	}

	public String getPluginOutLink(String projectName, String file) {
		return Constants.PROJECT_FILES_CONTEXT + "/" + HttpUtils.encode(projectName) + Constants.PLUGINS_OUTPUT_DIR + "/" + file;
	}

	public String getCommandOutputGuiLink(CommandExecutionStatusInfo j) {
		String project_name = j.project_name();
		String command = j.command();
		String path = String.valueOf(j.id());
		return j.finished() ? 
				Constants.RAW_OUTPUT_CONTEXT + ("?project=" + HttpUtils.encode(project_name) + "&resource=" + HttpUtils.encode(command) + "&link=" + HttpUtils.encode(getPluginOutLink(project_name, path))) : 
			getCommandOutputGui(project_name, command, path);
	}

	public String getCommandOutputGui(String project_name, String command, String path) {
		return Constants.PROGRESSIVE_RAW_OUTPUT_CONTEXT + "?project=" + HttpUtils.encode(project_name) + "&command=" + HttpUtils.encode(command) + "&path=" + HttpUtils.encode(String.valueOf(path));
	}
	
	public String getMonitorOutputGuiLink(String projectName, String peerName, String nodeName, String monitorName) {
		return Constants.RAW_OUTPUT_CONTEXT + ("?project=" + HttpUtils.encode(projectName) + "&link=" + HttpUtils.encode(getPeerMonitorResultLink(peerName, projectName, monitorName, nodeName)) + "&resource=" + HttpUtils.encode(monitorName));
	}
	
}
