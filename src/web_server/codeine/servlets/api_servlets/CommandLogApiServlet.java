package codeine.servlets.api_servlets;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeine.api.CommandStatusJson;
import codeine.command_peer.NodesCommandExecuterProvider;
import codeine.model.Constants;
import codeine.servlet.AbstractServlet;
import codeine.servlet.PermissionsManager;
import codeine.utils.JsonUtils;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class CommandLogApiServlet extends AbstractServlet {
	private static final long serialVersionUID = 1L;

	@Inject	private NodesCommandExecuterProvider nodesCommandExecuterProvider;
	@Inject private PermissionsManager permissionsManager;
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		List<CommandStatusJson> allCommands = nodesCommandExecuterProvider.getAllCommands(projectName);
		List<CommandStatusJson> allCommandsWithPermissions = Lists.newArrayList();
		for (CommandStatusJson commandStatusJson : allCommands) {
			CommandStatusJson c = JsonUtils.cloneJson(commandStatusJson, CommandStatusJson.class);
			c.can_cancel(permissionsManager.canCommand(c.project(), request));
		}
		writeResponseJson(response, allCommandsWithPermissions);
	}

}
