package codeine.servlets.api_servlets;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.api.ScehudleCommandExecutionInfo;
import codeine.command_peer.NodesCommandExecuterProvider;
import codeine.configuration.IConfigurationManager;
import codeine.configuration.Links;
import codeine.jsons.command.CommandInfo;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.servlet.AbstractServlet;
import codeine.servlet.PermissionsManager;
import codeine.utils.ExceptionUtils;

import com.google.inject.Inject;

public class NodesCommandApiServlet extends AbstractServlet {
	private static final Logger log = Logger.getLogger(NodesCommandApiServlet.class);
	private static final long serialVersionUID = 1L;

	@Inject private Links links;
	@Inject private NodesCommandExecuterProvider allNodesCommandExecuterProvider;
	@Inject private IConfigurationManager configurationManager;
	@Inject private PermissionsManager permissionsManager;
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		String projectName = getProjectName(request);
		if (!permissionsManager.canCommand(projectName, request)){
			return false;
		}
		return true;
	}
	
	private String getProjectName(HttpServletRequest request) {
		if (request.getMethod().equals("DELETE")) {
			return request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		}
		String data = request.getParameter(Constants.UrlParameters.DATA_NAME);
		ScehudleCommandExecutionInfo commandData = gson().fromJson(data, ScehudleCommandExecutionInfo.class);
		return commandData.command_info().project_name(); 
	}
	
	@Override
	protected void myPost(HttpServletRequest request, HttpServletResponse response) {
		log.debug("NodesCommandServlet request");
		String data = request.getParameter(Constants.UrlParameters.DATA_NAME);
		boolean redirect = Boolean.valueOf(request.getParameter(Constants.UrlParameters.REDIRECT));
		ScehudleCommandExecutionInfo commandData = gson().fromJson(data, ScehudleCommandExecutionInfo.class);
		String projectName = commandData.command_info().project_name();
		
		ProjectJson project = configurationManager.getProjectForName(projectName);
		CommandInfo configuredCommand = project.commandForName(commandData.command_info().command_name());
		overrideCommandInfoByConfiguration(commandData.command_info(), configuredCommand);
		long dir = allNodesCommandExecuterProvider.createExecutor().executeOnAllNodes(commandData);
		if (redirect){
			try {
				response.sendRedirect(links.getCommandOutputGui(projectName, commandData.command_info().command_name(), dir));
			} catch (IOException e) {
				throw ExceptionUtils.asUnchecked(e);
			}
		}
		else {
			writeResponseJson(response, dir);
		}
	}
	
	private void overrideCommandInfoByConfiguration(CommandInfo command_info, CommandInfo configuredCommand) {
		command_info.credentials(configuredCommand.credentials());
	}

	@Override
	protected void myDelete(HttpServletRequest request, HttpServletResponse response) {
		String project = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		String id = request.getParameter(Constants.UrlParameters.COMMAND_ID);
		allNodesCommandExecuterProvider.cancel(project, Long.valueOf(id));
	}

}
