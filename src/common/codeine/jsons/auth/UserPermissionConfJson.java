package codeine.jsons.auth;

import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.collect.Sets;

public class UserPermissionConfJson {

	
	private boolean administer;
	private Set<String> read_project = Sets.newHashSet();
	private Set<String> configure_project = Sets.newHashSet();
	private Set<String> command_project = Sets.newHashSet();
	
	protected UserPermissionConfJson() {
		super();
	}

	public UserPermissionConfJson(boolean administer) {
		this.administer = administer;
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
}
