package codeine.servlets.api_servlets.angular;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeine.jsons.info.CodeineRuntimeInfo;
import codeine.jsons.info.SessionInfo;
import codeine.servlet.AbstractServlet;
import codeine.servlet.PermissionsManager;

public class RuntimeInfoApiServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	@Inject private CodeineRuntimeInfo runtimeInfo;
	@Inject private PermissionsManager permissionsManager;

	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return true;
	}
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		writeResponseJson(response, new SessionInfo(runtimeInfo.version(), permissionsManager.user(request)));
	}

}
