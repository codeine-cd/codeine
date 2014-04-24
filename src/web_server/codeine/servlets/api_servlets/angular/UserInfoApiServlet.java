package codeine.servlets.api_servlets.angular;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeine.jsons.auth.CodeineUser;
import codeine.permissions.UserPermissionsGetter;
import codeine.servlet.AbstractApiServlet;

public class UserInfoApiServlet extends AbstractApiServlet {

	private @Inject UserPermissionsGetter permissionsManager;
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		CodeineUser codeineUser = permissionsManager.user(request).username();
		writeResponseJson(response, new UserInfo(codeineUser.username(), codeineUser.api_token()));
	}
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return true;
	}

}
