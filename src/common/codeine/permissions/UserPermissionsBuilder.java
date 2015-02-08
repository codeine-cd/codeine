package codeine.permissions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.configuration.IConfigurationManager;
import codeine.jsons.auth.CodeineUser;
import codeine.jsons.global.UserPermissionsJsonStore;
import codeine.jsons.project.ProjectJson;

import com.google.common.collect.Maps;

public class UserPermissionsBuilder {

	private static final Logger log = Logger.getLogger(UserPermissionsBuilder.class);
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
	
	public IUserWithPermissions getUserPermissions(CodeineUser user) {
		UserPermissions userPermissions = userPermissionsJsonStore.get().getOrNull(user.username());
		if (null == userPermissions) {
			userPermissions = new UserPermissions(user, false);
		}
		Map<String, UserProjectPermissions> p = getProjectPermissions(user.username());
		Map<String, UserPermissions> groupPermissions = getGroupsPermissions(user.username()); //group -> permissions
		Map<String, Map<String, UserProjectPermissions>> groupProjectsPermissions = getGroupsProjectsPermissions(user.username()); //group -> project -> permissions
		return new CompoundUserPermissions(userPermissions, p, groupPermissions, groupProjectsPermissions);
	}

	private HashMap<String, Map<String, UserProjectPermissions>> getGroupsProjectsPermissions(String user) {
		HashMap<String, Map<String, UserProjectPermissions>> $ = Maps.newHashMap();
		List<String> groups = groupsManager.groups(user);
		for (String group : groups) {
			log.info("group is " + group);
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
				log.info("u is " + u);
				if (u.username().equals(theUser)){
					p.put(projectJson.name(), u);
				}
			}
		}
		return p;
	}
}
