package codeine.users;

import java.util.Map.Entry;

import javax.inject.Inject;

import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.util.security.Credential;

import codeine.configuration.PathHelper;
import codeine.jsons.auth.IdentityConfJson;
import codeine.model.Constants;
import codeine.utils.JsonFileUtils;

public class UsersManager {

	@Inject private IdentityConfJson identityConfJson;
	@Inject private HashLoginService hashLoginService;
	private @Inject JsonFileUtils jsonFileUtils;
	private @Inject PathHelper pathHelper;
	
	private void store() {
		jsonFileUtils.setContent(Constants.getIdentityConfPath(), identityConfJson);
	}
	
	public LoginService loginService() {
		return hashLoginService;
	}

	public void initUsers() {
		for (Entry<String, String> i : identityConfJson.entries()) {
			String name = i.getKey();
			String credentials = i.getValue();
			putUser(name, credentials);
		}
	}
	
	private void putUser(String name, String credentials) {
		hashLoginService.putUser(name, Credential.getCredential(credentials), new String[] { "user" });
	}

	public void addUser(String name, String credentials) {
		identityConfJson.add(name, credentials);
		store();
		putUser(name, credentials);
	}
}
