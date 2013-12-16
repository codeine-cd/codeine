package codeine.jsons.global;

import codeine.jsons.JsonStore;
import codeine.jsons.auth.PermissionsConfJson;
import codeine.model.Constants;

public class PermissionsConfigurationJsonStore extends JsonStore<PermissionsConfJson>{

	public PermissionsConfigurationJsonStore() {
		super(Constants.getPermissionsConfPath(), PermissionsConfJson.class);
	}

}
