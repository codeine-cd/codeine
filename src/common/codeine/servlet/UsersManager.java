package codeine.servlet;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.util.security.Credential;

import codeine.jsons.auth.AuthenticationMethod;
import codeine.jsons.auth.CodeineUser;
import codeine.jsons.auth.IdentityConfJson;
import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.model.Constants;
import codeine.utils.ExceptionUtils;
import codeine.utils.JsonFileUtils;

public class UsersManager {

	private static final Logger log = Logger.getLogger(UsersManager.class);
	private @Inject IdentityConfJson identityConfJson;
	private @Inject HashLoginService hashLoginService;
	private @Inject JsonFileUtils jsonFileUtils;
	private @Inject GlobalConfigurationJsonStore globalConfigurationJsonStore;
	
	private void store() {
		jsonFileUtils.setContent(Constants.getIdentityConfPath(), identityConfJson);
	}
	
	public LoginService loginService() {
		return hashLoginService;
	}

	public void initUsers() {
		for (CodeineUser user : identityConfJson.entries()) {
			user.api_token();
			putUser(user.username(), user.credentials());
		}
	}
	
	private void putUser(String name, String credentials) {
		hashLoginService.putUser(name, Credential.getCredential(credentials), new String[] { "user" });
	}

	public CodeineUser addUser(String name, String credentials) {
		CodeineUser user = identityConfJson.add(name, credentials);
		store();
		putUser(name, credentials);
		return user;
	}
	
	public CodeineUser userByApiToken(String api_token) {
		for (CodeineUser user : identityConfJson.entries()) {
			if (user.api_token().equals(api_token))
				return user;
		}
		throw ExceptionUtils.asUnchecked(new IllegalArgumentException("No such user with api token " + api_token));
	}
	
	public CodeineUser user(String name) {
		for (CodeineUser user : identityConfJson.entries()) {
			if (user.username().equals(name)) return user;
		}
		if (globalConfigurationJsonStore.get().authentication_method() == AuthenticationMethod.WindowsCredentials) {
			log.info("creating automaticly user " + name);
			return addUser(name, "NONE");
		}
		throw ExceptionUtils.asUnchecked(new IllegalArgumentException("No such user " + name));
	}
	
	public boolean isUserExists(String username) {
		return hashLoginService.getUsers().containsKey(username);
	}

	public boolean hasUsers() {
		return hashLoginService.getUsers().size() > 0;
	}
}
