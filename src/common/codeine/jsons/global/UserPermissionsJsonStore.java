package codeine.jsons.global;

import codeine.jsons.JsonStore;
import codeine.model.Constants;
import codeine.permissions.PermissionsConfJson;

public class UserPermissionsJsonStore extends JsonStore<PermissionsConfJson>{

	public UserPermissionsJsonStore() {
		super(Constants.getPermissionsConfPath(), PermissionsConfJson.class);
	}

}
