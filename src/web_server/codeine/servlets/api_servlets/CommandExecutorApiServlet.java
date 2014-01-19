package codeine.servlets.api_servlets;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeine.api.CommandStatusJson;
import codeine.command_peer.NodesCommandExecuterProvider;
import codeine.servlet.AbstractServlet;
import codeine.servlet.PermissionsManager;
import codeine.utils.JsonUtils;

import com.google.common.collect.Lists;

public class CommandExecutorApiServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Inject	private NodesCommandExecuterProvider nodesCommandExecuterProvider;
	@Inject private PermissionsManager permissionsManager;
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response){
		List<CommandStatusJson> active = nodesCommandExecuterProvider.getActive();
		List<CommandStatusJson> activeWithPermissions = Lists.newArrayList();
		for (CommandStatusJson commandStatusJson : active) {
			CommandStatusJson c = JsonUtils.cloneJson(commandStatusJson, CommandStatusJson.class);
			c.can_cancel(permissionsManager.canCommand(c.project(), request));
			activeWithPermissions.add(c);
		}
		writeResponseJson(response, activeWithPermissions);
	}
	
}
