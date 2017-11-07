package codeine.servlet;

import codeine.jsons.auth.AuthenticationMethod;
import codeine.jsons.auth.CodeineUser;
import codeine.jsons.auth.IdentityConfJson;
import codeine.jsons.auth.IdentityConfJsonStore;
import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.plugins.CodeineConfModifyPlugin;
import codeine.plugins.CodeineConfModifyPlugin.Step;
import codeine.utils.ExceptionUtils;
import javax.inject.Inject;
import org.apache.log4j.Logger;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.util.security.Credential;

public class UsersManager {

	private static final Logger log = Logger.getLogger(UsersManager.class);
	private @Inject IdentityConfJsonStore identityConfJsonStore;
	private @Inject HashLoginService hashLoginService;
	private @Inject GlobalConfigurationJsonStore globalConfigurationJsonStore;
	private @Inject CodeineConfModifyPlugin codeineConfModifyPlugin;
	
	private void store(IdentityConfJson json) {
		identityConfJsonStore.store(json);
	}
	
	public LoginService loginService() {
		return hashLoginService;
	}

	public void initUsers() {
		for (CodeineUser user : identityConfJsonStore.get().entries()) {
			user.api_token();
			putUser(user.username(), user.credentials());
		}
	}
	
	private void putUser(String name, String sisma) {
		hashLoginService.putUser(name, Credential.getCredential(sisma), new String[] { "user" });
	}

	public CodeineUser addUser(String name, String sisma) {
		CodeineUser user = identityConfJsonStore.get().add(name, sisma);
		codeineConfModifyPlugin.call(Step.pre, name);
		store(identityConfJsonStore.get());
		codeineConfModifyPlugin.call(Step.post, name);
		putUser(name, sisma);
		return user;
	}
	
	public CodeineUser userByApiToken(String api_token) {
		for (CodeineUser user : identityConfJsonStore.get().entries()) {
			if (user.api_token().equals(api_token))
				return user;
		}
		throw ExceptionUtils.asUnchecked(new IllegalArgumentException("No such user with api token " + api_token));
	}
	
	public CodeineUser userOrGuest(String name) {
		for (CodeineUser user : identityConfJsonStore.get().entries()) {
			if (user.username().equals(name)) return user;
		}
		if (globalConfigurationJsonStore.get().authentication_method() == AuthenticationMethod.WindowsCredentials) {
			log.info("creating automaticly user " + name);
			return addUser(name, "NONE");
		}
		return CodeineUser.createGuest(name);
	}
	
	public boolean isUserExists(String username) {
		return hashLoginService.getUsers().containsKey(username);
	}

	public boolean hasUsers() {
		return hashLoginService.getUsers().size() > 0;
	}
}
