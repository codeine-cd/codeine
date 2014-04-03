package codeine.servlets.api_servlets.angular;

import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeine.configuration.IConfigurationManager;
import codeine.jsons.auth.IUserPermissions;
import codeine.jsons.auth.UserPermissions;
import codeine.jsons.info.CodeineRuntimeInfo;
import codeine.jsons.info.SessionInfo;
import codeine.jsons.project.ProjectJson;
import codeine.servlet.AbstractApiServlet;
import codeine.servlet.PermissionsManager;
import codeine.servlet.PrepareForShutdown;

import com.google.common.collect.Sets;

public class RuntimeInfoApiServlet extends AbstractApiServlet {

	private static final long serialVersionUID = 1L;
	@Inject private CodeineRuntimeInfo runtimeInfo;
	@Inject private PermissionsManager permissionsManager;
	@Inject private IConfigurationManager configurationManager;
	@Inject private PrepareForShutdown prepareForShutdown;

	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return true;
	}
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		IUserPermissions user = permissionsManager.user(request);
		Set<String> canCommand = Sets.newHashSet();
		Set<String> canConfigure = Sets.newHashSet();
		Set<String> canRead = Sets.newHashSet();
		for (ProjectJson p : configurationManager.getConfiguredProjects()) {
			if (user.canCommand(p.name())) {
				canCommand.add(p.name());
			}
			if (user.canConfigure(p.name())) {
				canConfigure.add(p.name());
			}
			if (user.canRead(p.name())) {
				canRead.add(p.name());
			}
		}
		UserPermissions userPermissions = new UserPermissions(user.username(), user.isAdministrator(), canRead, canCommand, canConfigure);
		writeResponseJson(response, new SessionInfo(runtimeInfo.version(), userPermissions, prepareForShutdown.isSequnceActivated()));
	}

}
