package codeine.jsons.auth;

import java.util.Map;

import com.google.common.collect.Lists;

public class CompoundUserPermissions implements IUserPermissions{

	private UserPermissions userPermissions;
	private Map<String, UserProjectPermissions> specificProjectConfiguration;
	
	public CompoundUserPermissions(UserPermissions userPermissions,
			Map<String, UserProjectPermissions> specificProjectConfiguration) {
		super();
		this.userPermissions = userPermissions;
		this.specificProjectConfiguration = specificProjectConfiguration;
	}
	@Override
	public boolean canRead(String projectName) {
		return userPermissions.canRead(projectName) || getForProject(projectName).canRead();
	}
	private UserProjectPermissions getForProject(String projectName) {
		if (specificProjectConfiguration.containsKey(projectName)) {
			return specificProjectConfiguration.get(projectName);
		}
		return createUnauthorizedUser();
	}
	private UserProjectPermissions createUnauthorizedUser() {
		return new UserProjectPermissions("unauthorized", false, Lists.<String>newArrayList());
	}
	@Override
	public boolean canCommand(String projectName) {
		return userPermissions.canCommand(projectName) || getForProject(projectName).canCommand();
	}
	@Override
	public boolean canConfigure(String projectName) {
		return userPermissions.canConfigure(projectName) || getForProject(projectName).canConfigure();
	}
	@Override
	public boolean isAdministrator() {
		return userPermissions.isAdministrator();
	}
	@Override
	public String username() {
		return userPermissions.username();
	}
}
