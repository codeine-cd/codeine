package codeine.servlets.api_servlets;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeine.api.CommandStatusJson;
import codeine.command_peer.NodesCommandExecuterProvider;
import codeine.permissions.IUserWithPermissions;
import codeine.permissions.UserPermissionsGetter;
import codeine.servlet.AbstractApiServlet;
import codeine.utils.JsonUtils;

import com.google.common.collect.Lists;

public class CommandExecutorApiServlet extends AbstractApiServlet {

	private static final long serialVersionUID = 1L;

	@Inject	private NodesCommandExecuterProvider nodesCommandExecuterProvider;
	@Inject private UserPermissionsGetter permissionsManager;
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response){
		setNoCache(response);
		List<CommandStatusJson> active = nodesCommandExecuterProvider.getActive();
		List<CommandStatusJson> activeWithPermissions = Lists.newArrayList();
		IUserWithPermissions user = permissionsManager.user(request);
		for (CommandStatusJson commandStatusJson : active) {
			if (user.canRead(commandStatusJson.project())){
				CommandStatusJson c = JsonUtils.cloneJson(commandStatusJson, CommandStatusJson.class);
				c.can_cancel(user.canCommand(c.project()));
				activeWithPermissions.add(c);
			}
		}
		writeResponseJson(response, activeWithPermissions);
	}
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return true;
	}
}
