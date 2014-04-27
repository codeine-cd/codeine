package codeine.permissions;

import java.util.List;
import java.util.Map;

import codeine.jsons.auth.CodeineUser;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class CompoundUserPermissions implements IUserWithPermissions{

	private UserPermissions userPermissions;
	private Map<String, UserProjectPermissions> specificProjectConfiguration;
	private Map<String, UserPermissions> groupsPermissions;
	private Map<String, Map<String, UserProjectPermissions>> groupsProjectsPermissions;
	
	public CompoundUserPermissions(UserPermissions userPermissions,
			Map<String, UserProjectPermissions> specificProjectConfiguration, Map<String, UserPermissions> groupsPermissions, Map<String, Map<String, UserProjectPermissions>> groupsProjectsPermissions) {
		super();
		this.userPermissions = userPermissions;
		this.specificProjectConfiguration = specificProjectConfiguration;
		this.groupsPermissions = groupsPermissions;
		this.groupsProjectsPermissions = groupsProjectsPermissions;
	}
	
	@Override
	public boolean canRead(String projectName) {
		for (UserPermissions userPermissions2 : getUserPermissions()) {
			if (userPermissions2.canRead(projectName)) {
				return true;
			}
		}
		for (Map<String, UserProjectPermissions> userProjectPermissions : getUserProjectPermissions()) {
			if (getForProject(userProjectPermissions, projectName).canRead()) {
				return true;
			}
		}
		return false;
	}
	private List<Map<String, UserProjectPermissions>> getUserProjectPermissions() {
		List<Map<String, UserProjectPermissions>> $ = Lists.newArrayList();
		$.add(specificProjectConfiguration);
		$.addAll(groupsProjectsPermissions.values());
		return $;
	}

	private List<UserPermissions> getUserPermissions() {
		List<UserPermissions> $ = Lists.newArrayList(userPermissions);
		$.addAll(groupsPermissions.values());
		return $;
	}

	private UserProjectPermissions getForProject(Map<String, UserProjectPermissions> userProjectPermissions, String projectName) {
		if (userProjectPermissions.containsKey(projectName)) {
			return userProjectPermissions.get(projectName);
		}
		return createUnauthorizedUser();
	}
	private UserProjectPermissions createUnauthorizedUser() {
		return new UserProjectPermissions("unauthorized", false, Sets.<String>newHashSet(), false);
	}
	@Override
	public boolean canCommand(String projectName) {
		for (UserPermissions userPermissions2 : getUserPermissions()) {
			if (userPermissions2.canCommand(projectName)) {
				return true;
			}
		}
		for (Map<String, UserProjectPermissions> userProjectPermissions : getUserProjectPermissions()) {
			if (getForProject(userProjectPermissions, projectName).canCommand()) {
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean canCommand(String projectName, String nodeAlias) {
		for (UserPermissions userPermissions2 : getUserPermissions()) {
			if (userPermissions2.canCommand(projectName)) {
				return true;
			}
		}
		for (Map<String, UserProjectPermissions> userProjectPermissions : getUserProjectPermissions()) {
			if (getForProject(userProjectPermissions, projectName).canCommand(nodeAlias)) {
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean canConfigure(String projectName) {
		for (UserPermissions userPermissions2 : getUserPermissions()) {
			if (userPermissions2.canConfigure(projectName)) {
				return true;
			}
		}
		for (Map<String, UserProjectPermissions> userProjectPermissions : getUserProjectPermissions()) {
			if (getForProject(userProjectPermissions, projectName).canConfigure()) {
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean isAdministrator() {
		for (UserPermissions userPermissions2 : getUserPermissions()) {
			if (userPermissions2.isAdministrator()) {
				return true;
			}
		}
		return false;
	}
	@Override
	public CodeineUser user() {
		return userPermissions.user();
	}
	
}
