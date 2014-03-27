package codeine.jsons.auth;

public interface IUserPermissions {

	public boolean canRead(String projectName);
	public boolean canCommand(String projectName);
	public boolean canConfigure(String projectName);
	public boolean isAdministrator();
	public String username();
}
