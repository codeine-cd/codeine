package codeine.jsons.auth;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Maps;

public class IdentityConfJson {
	
	private Map<String, String> users = Maps.newHashMap();
	
	public Set<Entry<String, String>> entries() {
		return users.entrySet();
	}

	public void add(String name, String credentials) {
		users.put(name, credentials);
	}

}
