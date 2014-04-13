package codeine.permissions;

import codeine.jsons.global.UserPermissionsJsonStore;

final class UserPermissionsJsonStoreForTest extends UserPermissionsJsonStore {
	private PermissionsConfJson json;
	
	public UserPermissionsJsonStoreForTest(PermissionsConfJson json) {
		super();
		this.json = json;
	}

	@Override
	public PermissionsConfJson get() {
		return json;
	}
}