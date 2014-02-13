package codeine.jsons.info;

import codeine.jsons.auth.UserPermissions;

public class SessionInfo {

	private String version;
	private UserPermissions permissions;
	
	public SessionInfo(String version, UserPermissions permissions) {
		this.version = version;
		this.permissions = permissions;
	}
	
}
