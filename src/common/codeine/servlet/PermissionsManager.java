package codeine.servlet;

import java.security.Principal;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import codeine.jsons.auth.PermissionsConfJson;
import codeine.model.Constants;

public class PermissionsManager {

	@Inject private PermissionsConfJson permissionConfJson;
	
	public boolean isModifiable(String projectName, HttpServletRequest request) {
		if (Boolean.getBoolean("ignoreSecurity")){
			return true;
		}
		if (!Constants.SECURITY_ENABLED){
			return "false".equals(request.getParameter("readonly"));
		}
		String user = user(request);
		if (user == null){
			return false;
		}
		return permissionConfJson.get(user).contains("all") || hasPermissions(projectName, user);
	}

	private boolean hasPermissions(String projectName, String user) {
		if (permissionConfJson.get(user).contains(projectName)){
			return true;
		}
		for (String entryOfUser : permissionConfJson.get(user)) {
			Pattern pattern = Pattern.compile(entryOfUser);
			if (pattern.matcher(projectName).matches()){
				return true;
			}
		}
		return false;
	}

	public String user(HttpServletRequest request) {
		Principal userPrincipal = request.getUserPrincipal();
		if (null == userPrincipal){
			return null;
		}
		String username = userPrincipal.getName();
		if (username.contains("@")){
			username = username.substring(0, username.indexOf("@"));
		}
		return username;
	}

}
