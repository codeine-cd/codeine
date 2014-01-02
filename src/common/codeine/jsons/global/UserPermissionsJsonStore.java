package codeine.jsons.global;

import codeine.jsons.JsonStore;
import codeine.jsons.auth.PermissionsConfJson;
import codeine.model.Constants;

public class UserPermissionsJsonStore extends JsonStore<PermissionsConfJson>{

	public UserPermissionsJsonStore() {
		super(Constants.getPermissionsConfPath(), PermissionsConfJson.class);
	}

}
