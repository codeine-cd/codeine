package codeine.servlets.api_servlets.angular;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeine.jsons.auth.UserPermissions;
import codeine.jsons.global.UserPermissionsJsonStore;
import codeine.servlet.AbstractServlet;
import codeine.servlet.PermissionsManager;

import com.google.inject.Inject;

public class CodeinePermissionsApiServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	private @Inject PermissionsManager permissionsManager;
	private @Inject UserPermissionsJsonStore permissionsJsonStore;
	

	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return permissionsManager.isAdministrator(request);
	}
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		List<UserPermissions> permissions = permissionsJsonStore.get().permissions();
		writeResponseJson(response, permissions);
	}

}
