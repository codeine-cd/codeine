package codeine.servlet;

import java.security.Principal;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import codeine.jsons.auth.AuthenticationMethod;
import codeine.jsons.auth.PermissionsConfJson;
import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.jsons.global.UserPermissionsJsonStore;
import codeine.model.Constants;
import codeine.utils.StringUtils;

public class PermissionsManager {

	private UserPermissionsJsonStore permissionsConfigurationJsonStore;
	private GlobalConfigurationJsonStore globalConfigurationJson;
	private UserPermissionsJsonStore permissionConfJson;
	private UsersManager usersManager;
		
	@Inject
	public PermissionsManager(UserPermissionsJsonStore permissionsConfigurationJsonStore,
			GlobalConfigurationJsonStore globalConfigurationJson, UserPermissionsJsonStore permissionConfJson, UsersManager usersManager) {
		super();
		this.permissionsConfigurationJsonStore = permissionsConfigurationJsonStore;
		this.globalConfigurationJson = globalConfigurationJson;
		this.permissionConfJson = permissionConfJson;
		this.usersManager = usersManager;
	}
	public boolean canRead(String projectName, HttpServletRequest request){
		if (ignoreSecurity()){
			return true;
		}
		if (permissionsNotConfigured(request)){
			return false;
		}
		return permissionConfJson.get().get(user(request)).canRead(projectName);
	}
	private boolean permissionsNotConfigured(HttpServletRequest request) {
		return user(request) == null || permissionConfJson.get().getOrNull(user(request)) == null;
	}
	private boolean ignoreSecurity() {
		return Boolean.getBoolean("ignoreSecurity") || globalConfigurationJson.get().authentication_method() == AuthenticationMethod.Disabled || !Constants.SECURITY_ENABLED;
	}
	public boolean canCommand(String projectName, HttpServletRequest request){
		if (ignoreSecurity()){
			return true;
		}
		if (permissionsNotConfigured(request)){
			return false;
		}
		return permissionConfJson.get().get(user(request)).canCommand(projectName);
	}
	public boolean isAdministrator(HttpServletRequest request){
		if (ignoreSecurity()){
			return true;
		}
		if (permissionsNotConfigured(request)){
			return false;
		}
		return permissionConfJson.get().get(user(request)).isAdministrator();
	}

	public String user(HttpServletRequest request) {
		String userFromCommandLine = System.getProperty("codeineUser");
		if (null != userFromCommandLine){
			return userFromCommandLine;
		}
		
		String api_token = request.getHeader(Constants.API_TOKEN);
		if (!StringUtils.isEmpty(api_token)) { 
			return usersManager.userByApiToken(api_token).username();
		}
		
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
	public boolean canConfigure(String projectName, HttpServletRequest request) {
		if (ignoreSecurity()){
			return true;
		}
		if (permissionsNotConfigured(request)){
			return false;
		}
		return permissionConfJson.get().get(user(request)).canConfigure(projectName);
	}
	public void makeAdmin(String user) {
		PermissionsConfJson permissionsConfJson = permissionConfJson.get();
		permissionsConfJson.makeAdmin(user);
		permissionsConfigurationJsonStore.store(permissionsConfJson);
	}

}
