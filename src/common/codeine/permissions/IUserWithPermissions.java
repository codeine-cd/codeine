package codeine.permissions;

import codeine.jsons.auth.CodeineUser;

public interface IUserWithPermissions {

	public boolean canRead(String projectName);
	public boolean canCommand(String projectName);
	public boolean canConfigure(String projectName);
	public boolean isAdministrator();
	public CodeineUser user();
	public boolean canCommand(String projectName, String nodeAlias);
}
