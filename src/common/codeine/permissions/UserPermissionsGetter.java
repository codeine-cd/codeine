package codeine.permissions;

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
import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.jsons.global.UserPermissionsJsonStore;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.servlet.UsersManager;
import codeine.utils.StringUtils;

import com.google.common.collect.Maps;

public class UserPermissionsGetter {


	private static final Logger log = Logger.getLogger(UserPermissionsGetter.class);
	public static final String IGNORE_SECURITY = "ignoreSecurity";
	
	private UserPermissionsJsonStore permissionsConfigurationJsonStore;
	private IConfigurationManager configurationManager;
	private GlobalConfigurationJsonStore globalConfigurationJson;
	private GroupsManager groupsManager;
	private UsersManager usersManager;
	private final UserPermissions ADMIN_GUEST = new UserPermissions("Guest", true);
	
	@Inject
	public UserPermissionsGetter(UserPermissionsJsonStore permissionsConfigurationJsonStore, 
			GlobalConfigurationJsonStore globalConfigurationJson, UsersManager usersManager, IConfigurationManager configurationManager, GroupsManager groupsManager) {
		super();
		this.permissionsConfigurationJsonStore = permissionsConfigurationJsonStore;
		this.globalConfigurationJson = globalConfigurationJson;
		this.usersManager = usersManager;
		this.configurationManager = configurationManager;
		this.groupsManager = groupsManager;
	}
	
	private boolean ignoreSecurity() {
		return Boolean.getBoolean(IGNORE_SECURITY) || globalConfigurationJson.get().authentication_method() == AuthenticationMethod.Disabled || !Constants.SECURITY_ENABLED;
	}
	
	public IUserPermissions user(HttpServletRequest request){
		if (ignoreSecurity()) {
			return ADMIN_GUEST;
		}
		String user = getUser(request);
		IUserPermissions userPermissions = getUserPermissions(user);
		return userPermissions; 
		
	}

	private IUserPermissions getUserPermissions(String user) {
		UserPermissions userPermissions = permissionsConfigurationJsonStore.get().getOrNull(user);
		if (null == userPermissions) {
			userPermissions = guest(user);
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
	
	private UserPermissions guest(String user) {
		return new UserPermissions(user, false);
	}
	
	private String getUser(HttpServletRequest request) {
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
		if (!StringUtils.isEmpty(viewas) && getUserPermissions(username).isAdministrator()) {
			CodeineUser user = usersManager.user(viewas);
			log.debug("Using VIEW_AS Mode - " + user.username());
			return user.username();
		}
		
		return username;
	}
	
}
