package codeine.jsons.auth;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Sets;

public class IdentityConfJson {
	
	private Set<CodeineUser> users = Sets.newHashSet();
	
	public Collection<CodeineUser> entries() {
		return Collections.unmodifiableCollection(users);
	}

	public CodeineUser add(String name, String credentials) {
		CodeineUser user = CodeineUser.createNewUser(name, credentials);
		users.add(user);
		return user;
	}

}
