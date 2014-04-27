package codeine.permissions;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import codeine.jsons.auth.AuthenticationMethod;
import codeine.jsons.auth.CodeineUser;
import codeine.jsons.global.GlobalConfigurationJson;
import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.utils.exceptions.UnAuthorizedException;

public class UserPermissionsGetterTest {

	@Mock
	private GlobalConfigurationJsonStore globalConfigurationJsonStore;
	@Mock
	private GlobalConfigurationJson globalConfigurationJson;
	@Mock
	private UserPermissionsBuilder userPermissionsBuilder;
	@Mock
	private UsernameResolverFromRequest usernameResolverFromRequest;
	@Mock
	private HttpServletRequest request;
	private UserPermissionsGetter tested;

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
		tested = new UserPermissionsGetter(userPermissionsBuilder,
				globalConfigurationJsonStore, usernameResolverFromRequest);
		when(globalConfigurationJsonStore.get()).thenReturn(globalConfigurationJson);
		when(globalConfigurationJson.authentication_method()).thenReturn(AuthenticationMethod.Builtin);
		when(usernameResolverFromRequest.getViewAsUser(request)).thenReturn(CodeineUser.createNewUser(null, "whatever"));
	}

	@Test
	public void testIgnoreSecurity() {
		System.setProperty(UserPermissionsGetter.IGNORE_SECURITY, "true");
		IUserWithPermissions user = tested.user(request);
		assertTrue(user.isAdministrator());
		assertEquals("Guest", user.user().username());
	}
	
	@After
	public void cleanup() {
		System.clearProperty(UserPermissionsGetter.IGNORE_SECURITY);
	}

	@Test
	public void testGuestUserForNonExitUserOrEmpty() {
		CodeineUser guestUser = CodeineUser.createNewUser("Guest", "whatever");
		when(usernameResolverFromRequest.getUser(request)).thenReturn(guestUser);
		tested.user(request);
		verify(userPermissionsBuilder).getUserPermissions(guestUser);
	}
	private CodeineUser createUser(String username) {
		return CodeineUser.createGuest(username);
	}
	private CodeineUser adminUser = createUser("Admin");
	private CodeineUser notAdmin = createUser("NotAdmin");
	private CodeineUser viewAs = createUser("viewas");
	
	@Test
	public void testViewAsUser() {
		when(usernameResolverFromRequest.getUser(request)).thenReturn(adminUser);
		when(userPermissionsBuilder.getUserPermissions(adminUser)).thenReturn(new UserPermissions(adminUser, true));
		when(usernameResolverFromRequest.getViewAsUser(request)).thenReturn(viewAs);
		when(userPermissionsBuilder.getUserPermissions(viewAs)).thenReturn(new UserPermissions(viewAs, false));
		IUserWithPermissions user = tested.user(request);
		assertFalse(user.isAdministrator());
		assertEquals("viewas", user.user().username());
	}
	@Test(expected=UnAuthorizedException.class)
	public void testViewAsUserNoAdmin() {
		when(usernameResolverFromRequest.getUser(request)).thenReturn(notAdmin);
		when(userPermissionsBuilder.getUserPermissions(notAdmin)).thenReturn(new UserPermissions(notAdmin, false));
		when(usernameResolverFromRequest.getViewAsUser(request)).thenReturn(viewAs);
		when(userPermissionsBuilder.getUserPermissions(viewAs)).thenReturn(new UserPermissions(viewAs, false));
		tested.user(request);
	}
	

}
