package codeine.jsons.auth;

import java.util.Map;

import com.google.common.collect.Maps;

public class PermissionsConfJson {

	private Map<String, UserPermissionConfJson> users_configuration = Maps.newHashMap();

	public UserPermissionConfJson get(String user) {
		return users_configuration.get(user);
	}

	public void makeAdmin(String user) {
		UserPermissionConfJson p = new UserPermissionConfJson(true);
		users_configuration.put(user, p);
	}

}
