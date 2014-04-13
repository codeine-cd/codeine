package codeine.permissions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import codeine.configuration.IConfigurationManager;
import codeine.jsons.global.UserPermissionsJsonStore;
import codeine.jsons.project.ProjectJson;

import com.google.common.collect.Maps;

public class UserPermissionsBuilder {

	private UserPermissionsJsonStore userPermissionsJsonStore;
	private IConfigurationManager configurationManager;
	private GroupsManager groupsManager;
	
	@Inject
	public UserPermissionsBuilder(UserPermissionsJsonStore userPermissionsJsonStore,
			IConfigurationManager configurationManager, GroupsManager groupsManager) {
		super();
		this.userPermissionsJsonStore = userPermissionsJsonStore;
		this.configurationManager = configurationManager;
		this.groupsManager = groupsManager;
	}
	
	public IUserPermissions getUserPermissions(String user) {
		UserPermissions userPermissions = userPermissionsJsonStore.get().getOrNull(user);
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
			UserPermissions userPermissions = userPermissionsJsonStore.get().getOrNull(group);
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
}
