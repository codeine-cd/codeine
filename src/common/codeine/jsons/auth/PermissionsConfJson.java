package codeine.jsons.auth;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class PermissionsConfJson {

	private Map<String, Set<String>> userToProjects = Maps.newHashMap();
	
	public Set<String> get(String user) {
		Set<String> set = userToProjects.get(user);
		if (null == set){
			return Sets.newHashSet();
		}
		return set;
	}

}
