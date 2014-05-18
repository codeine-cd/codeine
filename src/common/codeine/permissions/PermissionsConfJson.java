package codeine.permissions;

import java.util.List;

import org.apache.log4j.Logger;

import codeine.jsons.auth.CodeineUser;

import com.google.common.collect.Lists;

public class PermissionsConfJson {

	private static final Logger log = Logger
			.getLogger(PermissionsConfJson.class);
	private List<UserPermissions> permissions = Lists.newArrayList();

	public PermissionsConfJson() {
		
	}
	
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
			if (u.user() == null || u.user().username() == null) {
				log.warn("permissions contains non existing user " + u.usernameString(), new AssertionError());
				continue;
			}
			if (user.equals(u.user().username())) {
				return u;
			}
		}
		return null;
	}

	public List<UserPermissions> permissions() {
		return permissions;
	}
	public void makeAdmin(CodeineUser user) {
		UserPermissions p = new UserPermissions(user,true);
		permissions.add(p);
	}

}
