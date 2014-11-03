package codeine.servlets.api_servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.api.NodeGetter;
import codeine.api.ScehudleCommandExecutionInfo;
import codeine.command_peer.NodesCommandExecuterProvider;
import codeine.configuration.IConfigurationManager;
import codeine.jsons.command.CommandInfo;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.permissions.UserPermissionsGetter;
import codeine.servlet.AbstractApiServlet;
import codeine.utils.StringUtils;

import com.google.inject.Inject;

public class NodesCommandApiServlet extends AbstractApiServlet {
	private static final Logger log = Logger.getLogger(NodesCommandApiServlet.class);
	private static final long serialVersionUID = 1L;

	@Inject private NodesCommandExecuterProvider allNodesCommandExecuterProvider;
	@Inject private IConfigurationManager configurationManager;
	@Inject private UserPermissionsGetter permissionsManager;
	@Inject private NodeGetter nodeGetter;
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		String projectName = getProjectName(request);
		if (!permissionsManager.user(request).canCommand(projectName)){
			return false;
		}
		return true;
	}
	
	private String getProjectName(HttpServletRequest request) {
		String projectName = getParameter(request, Constants.UrlParameters.PROJECT_NAME);
		if (!StringUtils.isEmpty(projectName)) {
			return projectName;
		}
		String data = getData(request);
		ScehudleCommandExecutionInfo commandData = gson().fromJson(data, ScehudleCommandExecutionInfo.class);
		return commandData.command_info().project_name(); 
	}

	private String getData(HttpServletRequest request) {
		String data = getParameter(request, Constants.UrlParameters.DATA_NAME);
		if (StringUtils.isEmpty(data)) {
			data = readBody(request);
		}
		return data;
	}
	
	@Override
	protected void myPost(HttpServletRequest request, HttpServletResponse response) {
		log.info("NodesCommandServlet request");
		String data = getData(request);
		ScehudleCommandExecutionInfo commandData = gson().fromJson(data, ScehudleCommandExecutionInfo.class);
		String projectName = commandData.command_info().project_name();
		ProjectJson project = configurationManager.getProjectForName(projectName);
		String command_name = commandData.command_info().command_name();
		CommandInfo configuredCommand = project.commandForName(command_name);
		commandData.command_info().overrideByConfiguration(configuredCommand);
		updateNodes(commandData, projectName);
		long dir = allNodesCommandExecuterProvider.createExecutor().executeOnAllNodes(permissionsManager.user(request), commandData, project);
		manageStatisticsCollector().commandExecuted(projectName, command_name, String.valueOf(dir));
		writeResponseJson(response, dir);
	}
	
	private void updateNodes(ScehudleCommandExecutionInfo commandData, String projectName) {
		if (!commandData.should_execute_on_all_nodes()) {
			return;
		}
		log.info("will fetch all nodes for command execution");
		commandData.nodes().clear();
		commandData.nodes().addAll(nodeGetter.getNodes(projectName));
	}

	@Override
	protected void myDelete(HttpServletRequest request, HttpServletResponse response) {
		String project = getParameter(request, Constants.UrlParameters.PROJECT_NAME);
		String id = getParameter(request, Constants.UrlParameters.COMMAND_ID);
		allNodesCommandExecuterProvider.cancel(project, Long.valueOf(id));
	}

}
