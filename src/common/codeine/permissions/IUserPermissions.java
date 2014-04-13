package codeine.permissions;

public interface IUserPermissions {

	public boolean canRead(String projectName);
	public boolean canCommand(String projectName);
	public boolean canConfigure(String projectName);
	public boolean isAdministrator();
	public String username();
	public boolean canCommand(String projectName, String nodeAlias);
}
