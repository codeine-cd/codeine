package codeine.permissions;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import codeine.jsons.auth.AuthenticationMethod;
import codeine.jsons.auth.CodeineUser;
import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.model.Constants;
import codeine.utils.StringUtils;
import codeine.utils.exceptions.UnAuthorizedException;

public class UserPermissionsGetter {

	private static final Logger log = Logger.getLogger(UserPermissionsGetter.class);
	public static final String IGNORE_SECURITY = "ignoreSecurity";
	private final UserPermissions ADMIN_GUEST = new UserPermissions("Guest", true);

	private UserPermissionsBuilder userPermissionsBuilder;
	private GlobalConfigurationJsonStore globalConfigurationJson;
	private UsernameResolverFromRequest usernameResolverFromRequest;

	@Inject
	public UserPermissionsGetter(UserPermissionsBuilder userPermissionsBuilder,
			GlobalConfigurationJsonStore globalConfigurationJson,
			UsernameResolverFromRequest usernameResolverFromRequest) {
		super();
		this.userPermissionsBuilder = userPermissionsBuilder;
		this.globalConfigurationJson = globalConfigurationJson;
		this.usernameResolverFromRequest = usernameResolverFromRequest;
	}

	private boolean ignoreSecurity() {
		return Boolean.getBoolean(IGNORE_SECURITY)
				|| globalConfigurationJson.get().authentication_method() == AuthenticationMethod.Disabled
				|| !Constants.SECURITY_ENABLED;
	}

	public IUserPermissions user(HttpServletRequest request) {
		if (ignoreSecurity()) {
			return ADMIN_GUEST;
		}
		CodeineUser user = usernameResolverFromRequest.getUser(request);
		IUserPermissions userPermissions = userPermissionsBuilder.getUserPermissions(user.username());
		CodeineUser viewas = usernameResolverFromRequest.getViewAsUser(request);
		if (!StringUtils.isEmpty(viewas.username())) {
			if (!userPermissions.isAdministrator()) {
				throw new UnAuthorizedException("user " + user.username() + " is not admin!");
			}
			log.debug("Using VIEW_AS Mode - " + viewas);
			return userPermissionsBuilder.getUserPermissions(viewas.username());
		} else {
			return userPermissions;
		}
	}

	

}
