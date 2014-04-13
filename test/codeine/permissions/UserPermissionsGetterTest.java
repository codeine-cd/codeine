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
		IUserPermissions user = tested.user(request);
		assertTrue(user.isAdministrator());
		assertEquals("Guest", user.username());
	}
	
	@After
	public void cleanup() {
		System.clearProperty(UserPermissionsGetter.IGNORE_SECURITY);
	}

	@Test
	public void testGuestUserForNonExitUserOrEmpty() {
		when(usernameResolverFromRequest.getUser(request)).thenReturn(CodeineUser.createNewUser("Guest", "whatever"));
		tested.user(request);
		verify(userPermissionsBuilder).getUserPermissions("Guest");
	}
	@Test
	public void testViewAsUser() {
		when(usernameResolverFromRequest.getUser(request)).thenReturn(CodeineUser.createNewUser("Admin", "whatever"));
		when(userPermissionsBuilder.getUserPermissions("Admin")).thenReturn(new UserPermissions("Admin", true));
		when(usernameResolverFromRequest.getViewAsUser(request)).thenReturn(CodeineUser.createNewUser("viewas", "whatever"));
		when(userPermissionsBuilder.getUserPermissions("viewas")).thenReturn(new UserPermissions("viewas", false));
		IUserPermissions user = tested.user(request);
		assertFalse(user.isAdministrator());
		assertEquals("viewas", user.username());
	}
	@Test(expected=UnAuthorizedException.class)
	public void testViewAsUserNoAdmin() {
		when(usernameResolverFromRequest.getUser(request)).thenReturn(CodeineUser.createNewUser("NotAdmin", "whatever"));
		when(userPermissionsBuilder.getUserPermissions("NotAdmin")).thenReturn(new UserPermissions("NotAdmin", false));
		when(usernameResolverFromRequest.getViewAsUser(request)).thenReturn(CodeineUser.createNewUser("viewas", "whatever"));
		when(userPermissionsBuilder.getUserPermissions("viewas")).thenReturn(new UserPermissions("viewas", false));
		tested.user(request);
	}
	

}
