package codeine.permissions;

import java.security.Principal;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import codeine.jsons.auth.CodeineUser;
import codeine.model.Constants;
import codeine.servlet.UsersManager;
import codeine.utils.StringUtils;

public class UsernameResolverFromRequest {

	private static final Logger log = Logger.getLogger(UsernameResolverFromRequest.class);
	private UsersManager usersManager;

	@Inject
	public UsernameResolverFromRequest(UsersManager usersManager) {
		super();
		this.usersManager = usersManager;
	}

	public CodeineUser getUser(HttpServletRequest request) {
		String api_token = request.getHeader(Constants.API_TOKEN);
		if (!StringUtils.isEmpty(api_token)) { 
			return usersManager.userByApiToken(api_token);
		}
		Principal userPrincipal = request.getUserPrincipal();
		if (null == userPrincipal){
			return CodeineUser.createGuestUser();
		}
		String username = userPrincipal.getName();
		log.debug("handling request from user " + username);
		if (username.contains("@")){
			username = username.substring(0, username.indexOf("@"));
		}
		return usersManager.userOrGuest(username);
	}
	
	public CodeineUser getViewAsUser(HttpServletRequest request) {
		return CodeineUser.createNewUser(request.getHeader(Constants.UrlParameters.VIEW_AS), "blabla");
	}
}
