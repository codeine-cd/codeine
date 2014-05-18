package codeine.jsons.global;

import java.util.List;

import javax.inject.Inject;

import codeine.jsons.JsonStore;
import codeine.jsons.auth.CodeineUser;
import codeine.jsons.auth.IdentityConfJsonStore;
import codeine.model.Constants;
import codeine.permissions.PermissionsConfJson;
import codeine.permissions.UserPermissions;

import com.google.common.collect.Lists;

public class UserPermissionsJsonStore extends JsonStore<PermissionsConfJson>{

	@Inject private IdentityConfJsonStore identityConfJsonStore;
	
	public UserPermissionsJsonStore() {
		super(Constants.getPermissionsConfPath(), PermissionsConfJson.class);
	}

	@Override
	public PermissionsConfJson get() {
		List<UserPermissions> permissions = Lists.newArrayList();
		for (UserPermissions p : super.get().permissions()) {
			if (p.user() == null) {
				CodeineUser codeineUser = identityConfJsonStore.get().getOrNull(p.usernameString());
				if (null == codeineUser) {
					codeineUser = CodeineUser.createNewUser(p.usernameString(), "non-exist-user");
				}
				p.initUser(codeineUser);
			}
			permissions.add(p);
		}
		return new PermissionsConfJson(permissions);
	}
}
