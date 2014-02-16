package codeine.jsons.info;

import codeine.jsons.auth.UserPermissions;

@SuppressWarnings("unused")
public class SessionInfo {

	private String version;
	private UserPermissions permissions;
	private boolean isPrepareForShutdown;
	
	public SessionInfo(String version, UserPermissions permissions, boolean isPrepareForShutdown) {
		this.version = version;
		this.permissions = permissions;
		this.isPrepareForShutdown = isPrepareForShutdown;
	}
	
}
