package codeine.permissions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import codeine.configuration.IConfigurationManager;
import codeine.jsons.auth.AuthenticationMethod;
import codeine.jsons.auth.CodeineUser;
import codeine.jsons.global.GlobalConfigurationJson;
import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.jsons.global.UserPermissionsJsonStore;
import codeine.model.Constants;
import codeine.servlet.UsersManager;

public class UserPermissionsGetterTest {

	@Mock
	private UserPermissionsJsonStore permissionsConfigurationJsonStore;
	@Mock
	private GlobalConfigurationJsonStore globalConfigurationJsonStore;
	@Mock
	private UsersManager usersManager;
	@Mock
	private IConfigurationManager configurationManager;
	@Mock
	private GlobalConfigurationJson globalConfigurationJson;
	@Mock
	private GroupsManager groupsManager;
	@Mock
	private HttpServletRequest request;
	private UserPermissionsGetter tested;
	private PermissionsConfJson permissionsConfJson = new PermissionsConfJson();

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
		tested = new UserPermissionsGetter(permissionsConfigurationJsonStore,
				globalConfigurationJsonStore, usersManager, configurationManager, groupsManager);
		when(globalConfigurationJsonStore.get()).thenReturn(globalConfigurationJson);
		when(globalConfigurationJson.authentication_method()).thenReturn(AuthenticationMethod.Builtin);
		when(permissionsConfigurationJsonStore.get()).thenReturn(permissionsConfJson);
	}

	@Test
	public void testIgnoreSecurity() {
		System.setProperty(UserPermissionsGetter.IGNORE_SECURITY, "true");
		IUserPermissions user = tested.user(request);
		assertTrue(user.isAdministrator());
		assertEquals("Guest", user.username());
	}
	
	@After
	public void cleanup() {
		System.clearProperty(UserPermissionsGetter.IGNORE_SECURITY);
	}

	@Test
	public void testGuestUser() {
		IUserPermissions user = tested.user(request);
		assertFalse(user.isAdministrator());
		assertEquals("Guest", user.username());
	}
	
	@Test
	public void testApiToken() {
		when(request.getHeader(Constants.API_TOKEN)).thenReturn("token");
		CodeineUser newUser = CodeineUser.createNewUser("user", "password");
		when(usersManager.userByApiToken("token")).thenReturn(newUser);
		tested.user(request);
		Mockito.verify(usersManager).userByApiToken("token");
	}
//	@Test
//	public void testUsernameWithShtrudel() {
//		final String userWithDomain = "oshai@oshai";
//		Principal principal = new Principal() {
//			@Override
//			public String getName() {
//				return userWithDomain;
//			}
//		};
//		when(request.getUserPrincipal()).thenReturn(principal);
//		CodeineUser newUser = CodeineUser.createNewUser("user", "password");
//		when(usersManager.userByApiToken("token")).thenReturn(newUser);
//		tested.user(request);
//		Mockito.verify(usersManager).userByApiToken("token");
//	}

}
