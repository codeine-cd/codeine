package codeine.permissions;

import java.util.Set;
import java.util.regex.Pattern;

public abstract class AbstractUserPermissions {

	protected boolean isSetMatch(Set<String> set, String projectName){
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
}
