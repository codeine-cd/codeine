package codeine.permissions;

import java.util.List;

import com.google.common.collect.Lists;

public class PermissionsConfJson {

	private List<UserPermissions> permissions = Lists.newArrayList();

	public PermissionsConfJson() {}
	
	public PermissionsConfJson(List<UserPermissions> permissions) {
		this.permissions = permissions;
	}
	
	public UserPermissions get(String user) {
		UserPermissions userPermissions = getOrNull(user);
		if (null == userPermissions) {
			throw new IllegalArgumentException("user does not have permissions " + user);
		}
		return userPermissions;
	}
	public UserPermissions getOrNull(String user) {
		for (UserPermissions u : permissions) {
			if (user.equals(u.username())) {
				return u;
			}
		}
		return null;
	}

	public List<UserPermissions> permissions() {
		return permissions;
	}
	public void makeAdmin(String user) {
		UserPermissions p = new UserPermissions(user,true);
		permissions.add(p);
	}

}
