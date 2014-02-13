package codeine.jsons.auth;

import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.collect.Sets;

public class UserPermissions {

	
	private String username;
	private boolean administer;
	private Set<String> read_project = Sets.newHashSet();
	private Set<String> configure_project = Sets.newHashSet();
	private Set<String> command_project = Sets.newHashSet();
	
	protected UserPermissions() {
		super();
	}

	public UserPermissions(String username, boolean administer) {
		this.username = username;
		this.administer = administer;
	}

	public UserPermissions(String username, boolean administrator, Set<String> canRead, Set<String> canCommand,
			Set<String> canConfigure) {
		this(username, administrator);
		this.read_project = canRead;
		this.command_project = canCommand;
		this.configure_project = canConfigure;
	}

	public String username()
	{
		return username;
	}
	
	public boolean canRead(String projectName) {
		return isSetMatch(read_project, projectName) || canCommand(projectName);
	}


	public boolean canCommand(String projectName) {
		return isSetMatch(command_project, projectName) || canConfigure(projectName);
	}


	public boolean isAdministrator() {
		return administer;
	}
	
	private boolean isSetMatch(Set<String> set, String projectName){
		if (set.contains("all") || set.contains(projectName)){
			return true;
		}
		for (String key : set) {
			Pattern pattern = Pattern.compile(key);
			if (pattern.matcher(projectName).matches()){
				return true;
			}
		}
		return false;
	}


	public boolean canConfigure(String projectName) {
		return isSetMatch(configure_project, projectName) || isAdministrator();
	}

	@Override
	public String toString() {
		return "UserPermissions [username=" + username + ", administer=" + administer + ", read_project="
				+ read_project + ", configure_project=" + configure_project + ", command_project=" + command_project
				+ "]";
	}

	
	
	
}
