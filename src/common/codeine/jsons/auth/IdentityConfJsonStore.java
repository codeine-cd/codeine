package codeine.jsons.auth;

import codeine.jsons.JsonStore;
import codeine.model.Constants;

public class IdentityConfJsonStore extends JsonStore<IdentityConfJson>{

	public IdentityConfJsonStore() {
		super(Constants.getIdentityConfPath(), IdentityConfJson.class);
	}

	
}
