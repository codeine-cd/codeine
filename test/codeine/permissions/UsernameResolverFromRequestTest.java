package codeine.permissions;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import codeine.jsons.auth.CodeineUser;
import codeine.model.Constants;
import codeine.servlet.UsersManager;

public class UsernameResolverFromRequestTest {

	@Mock
	private HttpServletRequest request;
	@Mock
	private UsersManager usersManager;
	private UsernameResolverFromRequest tested;
	
	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
		tested = new UsernameResolverFromRequest(usersManager);
	}

	@Test
	public void testApiToken() {
		when(request.getHeader(Constants.API_TOKEN)).thenReturn("token");
		CodeineUser newUser = CodeineUser.createNewUser("user", "password");
		when(usersManager.userByApiToken("token")).thenReturn(newUser);
		CodeineUser user = tested.getUser(request);
		assertEquals(newUser, user);
	}
	@Test
	public void testGuestUser() {
		CodeineUser user = tested.getUser(request);
		assertEquals("Guest", user.username());
	}
	@Test
	public void testUsernameWithShtrudel() {
		final String userWithDomain = "oshai@oshai";
		Principal principal = new Principal() {
			@Override
			public String getName() {
				return userWithDomain;
			}
		};
		when(request.getUserPrincipal()).thenReturn(principal);
		@SuppressWarnings("unused")
		CodeineUser user = tested.getUser(request);
//		assertEquals("oshai", user.username());
		verify(usersManager).userOrGuest("oshai");
	}
}
