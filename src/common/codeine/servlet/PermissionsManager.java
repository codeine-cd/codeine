package codeine.servlet;

import java.security.Principal;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import codeine.jsons.auth.AuthenticationMethod;
import codeine.jsons.auth.PermissionsConfJson;
import codeine.jsons.global.GlobalConfigurationJson;
import codeine.jsons.global.PermissionsConfigurationJsonStore;
import codeine.model.Constants;

public class PermissionsManager {

	private PermissionsConfigurationJsonStore permissionsConfigurationJsonStore;
	private GlobalConfigurationJson globalConfigurationJson;
	private PermissionsConfJson permissionConfJson;
	
	
	@Inject
	public PermissionsManager(PermissionsConfigurationJsonStore permissionsConfigurationJsonStore,
			GlobalConfigurationJson globalConfigurationJson, PermissionsConfJson permissionConfJson) {
		super();
		this.permissionsConfigurationJsonStore = permissionsConfigurationJsonStore;
		this.globalConfigurationJson = globalConfigurationJson;
		this.permissionConfJson = permissionConfJson;
	}
	public boolean canRead(String projectName, HttpServletRequest request){
		if (ignoreSecurity()){
			return true;
		}
		if (permissionsNotConfigured(request)){
			return false;
		}
		return permissionConfJson.get(user(request)).canRead(projectName);
	}
	private boolean permissionsNotConfigured(HttpServletRequest request) {
		return user(request) == null || permissionConfJson.get(user(request)) == null;
	}
	private boolean ignoreSecurity() {
		return Boolean.getBoolean("ignoreSecurity") || globalConfigurationJson.authentication_method() == AuthenticationMethod.Disabled || !Constants.SECURITY_ENABLED;
	}
	public boolean canCommand(String projectName, HttpServletRequest request){
		if (ignoreSecurity()){
			return true;
		}
		if (permissionsNotConfigured(request)){
			return false;
		}
		return permissionConfJson.get(user(request)).canCommand(projectName);
	}
	public boolean isAdministrator(HttpServletRequest request){
		if (ignoreSecurity()){
			return true;
		}
		if (permissionsNotConfigured(request)){
			return false;
		}
		return permissionConfJson.get(user(request)).isAdministrator();
	}

	public String user(HttpServletRequest request) {
		String userFromCommandLine = System.getProperty("codeineUser");
		if (null != userFromCommandLine){
			return userFromCommandLine;
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
		return permissionConfJson.get(user(request)).canConfigure(projectName);
	}
	public void makeAdmin(String user) {
		permissionConfJson.makeAdmin(user);
		permissionsConfigurationJsonStore.store(permissionConfJson);
	}

}
