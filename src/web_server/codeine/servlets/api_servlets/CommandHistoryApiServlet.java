package codeine.servlets.api_servlets;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeine.api.CommandStatusJson;
import codeine.command_peer.NodesCommandExecuterProvider;
import codeine.model.Constants;
import codeine.permissions.UserPermissionsGetter;
import codeine.servlet.AbstractApiServlet;
import codeine.utils.JsonUtils;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class CommandHistoryApiServlet extends AbstractApiServlet {
	private static final long serialVersionUID = 1L;

	@Inject	private NodesCommandExecuterProvider nodesCommandExecuterProvider;
	@Inject private UserPermissionsGetter permissionsManager;
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		setNoCache(response);
		String projectName = getParameter(request, Constants.UrlParameters.PROJECT_NAME);
		String nodeName = getParameter(request, Constants.UrlParameters.NODE);
		List<CommandStatusJson> allCommands = nodesCommandExecuterProvider.getAllCommands(projectName, nodeName);
		List<CommandStatusJson> allCommandsWithPermissions = Lists.newArrayList();
		for (CommandStatusJson commandStatusJson : allCommands) {
			if (permissionsManager.user(request).canRead(commandStatusJson.project())){
				CommandStatusJson c = JsonUtils.cloneJson(commandStatusJson, CommandStatusJson.class);
				c.can_cancel(permissionsManager.user(request).canCommand(c.project()) && !c.finished());
				allCommandsWithPermissions.add(c);
			}
		}
		writeResponseJson(response, allCommandsWithPermissions);
	}

	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return true;
	}
}
