package codeine.servlets.api_servlets.angular;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeine.jsons.auth.CodeineUser;
import codeine.servlet.AbstractServlet;
import codeine.servlet.PermissionsManager;
import codeine.servlet.UsersManager;

public class UserInfoApiServlet extends AbstractServlet {

	private @Inject PermissionsManager permissionsManager;
	private @Inject UsersManager usersManager;
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		String user = permissionsManager.user(request).username();
		CodeineUser codeineUser = usersManager.user(user);
		writeResponseJson(response, new UserInfo(codeineUser.username(), codeineUser.api_token()));
	}
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return true;
	}

}
