package codeine.servlet;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import codeine.configuration.IConfigurationManager;
import codeine.jsons.auth.AuthenticationMethod;
import codeine.jsons.auth.CodeineUser;
import codeine.jsons.auth.CompoundUserPermissions;
import codeine.jsons.auth.IUserPermissions;
import codeine.jsons.auth.PermissionsConfJson;
import codeine.jsons.auth.UserPermissions;
import codeine.jsons.auth.UserProjectPermissions;
import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.jsons.global.UserPermissionsJsonStore;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.permissions.GroupsManager;
import codeine.utils.StringUtils;

import com.google.common.collect.Maps;

public class PermissionsManager {

	private static final Logger log = Logger.getLogger(PermissionsManager.class);
	
	private UserPermissionsJsonStore permissionsConfigurationJsonStore;
	private IConfigurationManager configurationManager;
	private GlobalConfigurationJsonStore globalConfigurationJson;
	private GroupsManager groupsManager;
	private UsersManager usersManager;
	private final UserPermissions ADMIN_GUEST = new UserPermissions("Guest", true);
	
	@Inject
	public PermissionsManager(UserPermissionsJsonStore permissionsConfigurationJsonStore, 
			GlobalConfigurationJsonStore globalConfigurationJson, UsersManager usersManager, IConfigurationManager configurationManager, GroupsManager groupsManager) {
		super();
		this.permissionsConfigurationJsonStore = permissionsConfigurationJsonStore;
		this.globalConfigurationJson = globalConfigurationJson;
		this.usersManager = usersManager;
		this.configurationManager = configurationManager;
		this.groupsManager = groupsManager;
	}
	
	public boolean canRead(String projectName, HttpServletRequest request){
		return user(request).canRead(projectName);
	}
	
	private boolean ignoreSecurity() {
		return Boolean.getBoolean("ignoreSecurity") || globalConfigurationJson.get().authentication_method() == AuthenticationMethod.Disabled || !Constants.SECURITY_ENABLED;
	}
	
	public boolean canCommand(String projectName, HttpServletRequest request){
		return user(request).canCommand(projectName);
	}
	
	public boolean canCommand(String projectName, String nodeAlias, HttpServletRequest request) {
		return user(request).canCommand(projectName, nodeAlias);
	}
	
	public boolean isAdministrator(HttpServletRequest request){
		return user(request).isAdministrator();
	}
	public IUserPermissions user(HttpServletRequest request){
		if (ignoreSecurity()) {
			return ADMIN_GUEST;
		}
		String user = userInternal(request);
		IUserPermissions userPermissions = getUser(user);
		return null == userPermissions ? guest(user) : userPermissions; 
		
	}

	private IUserPermissions getUser(String user) {
		UserPermissions userPermissions = permissionsConfigurationJsonStore.get().getOrNull(user);
		if (null == userPermissions) {
			return null;
		}
		Map<String, UserProjectPermissions> p = getProjectPermissions(user);
		Map<String, UserPermissions> groupPermissions = getGroupsPermissions(user); //group -> permissions
		Map<String, Map<String, UserProjectPermissions>> groupProjectsPermissions = getGroupsProjectsPermissions(user); //group -> project -> permissions
		return new CompoundUserPermissions(userPermissions, p, groupPermissions, groupProjectsPermissions);
	}

	private HashMap<String, Map<String, UserProjectPermissions>> getGroupsProjectsPermissions(String user) {
		HashMap<String, Map<String, UserProjectPermissions>> $ = Maps.newHashMap();
		List<String> groups = groupsManager.groups(user);
		for (String group : groups) {
			Map<String, UserProjectPermissions> projectPermissions = getProjectPermissions(group);
			if (!projectPermissions.isEmpty()) {
				$.put(group, projectPermissions);
			}
		}
		return $;
	}

	private HashMap<String, UserPermissions> getGroupsPermissions(String user) {
		HashMap<String, UserPermissions> $ = Maps.newHashMap();
		List<String> groups = groupsManager.groups(user);
		for (String group : groups) {
			UserPermissions userPermissions = permissionsConfigurationJsonStore.get().getOrNull(group);
			if (null != userPermissions) {
				$.put(group, userPermissions);
			}
		}
		return $;
	}

	private Map<String, UserProjectPermissions> getProjectPermissions(String theUser) {
		List<ProjectJson> configuredProjects = configurationManager.getConfiguredProjects();
		Map<String, UserProjectPermissions> p = Maps.newHashMap();
		for (ProjectJson projectJson : configuredProjects) {
			for (UserProjectPermissions u : projectJson.permissions()) {
				if (u.username().equals(theUser)){
					p.put(projectJson.name(), u);
				}
			}
		}
		return p;
	}
	
	private final UserPermissions guest(String user) {
		return new UserPermissions(user, false);
	}
	
	private String userInternal(HttpServletRequest request) {
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
			return "Guest";
		}
		
		String username = userPrincipal.getName();
		log.debug("handling request from user " + username);
		if (username.contains("@")){
			username = username.substring(0, username.indexOf("@"));
		}
		
		String viewas = request.getHeader(Constants.UrlParameters.VIEW_AS);
		if (!StringUtils.isEmpty(viewas) && getUser(username).isAdministrator()) {
			CodeineUser user = usersManager.user(viewas);
			log.debug("Using VIEW_AS Mode - " + user.username());
			return user.username();
		}
		
		return username;
	}
	public boolean canConfigure(String projectName, HttpServletRequest request) {
		return user(request).canConfigure(projectName);
	}
	public void makeAdmin(String user) {
		PermissionsConfJson permissionsConfJson = permissionsConfigurationJsonStore.get();
		permissionsConfJson.makeAdmin(user);
		permissionsConfigurationJsonStore.store(permissionsConfJson);
	}

}
