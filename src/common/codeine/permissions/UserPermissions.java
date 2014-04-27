package codeine.permissions;

import java.util.Set;

import codeine.jsons.auth.CodeineUser;

import com.google.common.collect.Sets;

public class UserPermissions extends AbstractUserPermissions implements IUserWithPermissions{

	private transient CodeineUser user;
	private String username;
	private boolean administer;
	private Set<String> read_project = Sets.newHashSet();
	private Set<String> configure_project = Sets.newHashSet();
	private Set<String> command_project = Sets.newHashSet();
	
	public UserPermissions() {
		super();
	}

	public UserPermissions(CodeineUser user, boolean administer) {
		this.user = user;
		this.username = user.username();
		this.administer = administer;
	}

	public UserPermissions(CodeineUser user, boolean administrator, Set<String> canRead, Set<String> canCommand,
			Set<String> canConfigure) {
		this(user, administrator);
		this.read_project = canRead;
		this.command_project = canCommand;
		this.configure_project = canConfigure;
	}

	@Override
	public CodeineUser user() {
		return user;
	}
	public String usernameString() {
		return username;
	}
	
	@Override
	public boolean canRead(String projectName) {
		return isSetMatch(read_project, projectName) || canCommand(projectName);
	}


	@Override
	public boolean canCommand(String projectName) {
		return isSetMatch(command_project, projectName) || canConfigure(projectName);
	}


	@Override
	public boolean isAdministrator() {
		return administer;
	}
	
	@Override
	public boolean canConfigure(String projectName) {
		return isSetMatch(configure_project, projectName) || isAdministrator();
	}

	@Override
	public String toString() {
		return "UserPermissions [username=" + username + ", administer=" + administer + ", read_project="
				+ read_project + ", configure_project=" + configure_project + ", command_project=" + command_project
				+ "]";
	}

	@Override
	public boolean canCommand(String projectName, String nodeAlias) {
		return canCommand(projectName);
	}

	public void initUser(CodeineUser codeineUser) {
		this.user = codeineUser;
	}

	
	
	
}
