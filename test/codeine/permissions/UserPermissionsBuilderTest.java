package codeine.permissions;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import codeine.configuration.IConfigurationManager;
import codeine.jsons.auth.CodeineUser;
import codeine.jsons.global.UserPermissionsJsonStore;

public class UserPermissionsBuilderTest {

	@Mock
	private UserPermissionsJsonStore userPermissionsJsonStore;
	private PermissionsConfJson userPermissionsJson = new PermissionsConfJson();
	@Mock
	private IConfigurationManager configurationManager;
	@Mock
	private GroupsManager groupsManager;
	private UserPermissionsBuilder tested;
	
	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
		when(userPermissionsJsonStore.get()).thenReturn(userPermissionsJson);
		tested = new UserPermissionsBuilder(userPermissionsJsonStore, configurationManager, groupsManager);
	}
	
	@Test
	public void testNonExistUser() {
		IUserWithPermissions userPermissions = tested.getUserPermissions(createUser("not_exist"));
		assertEquals("not_exist", userPermissions.user().username());
		assertFalse(userPermissions.isAdministrator());
	}
	private CodeineUser createUser(String username) {
		return CodeineUser.createGuest(username);
	}

	@Test
	public void testExistUser() {
		UserPermissions newUserPermissions = new UserPermissions(createUser("user"), false);
		userPermissionsJson.permissions().add(newUserPermissions);
		IUserWithPermissions userPermissions = tested.getUserPermissions(createUser("user"));
		assertEquals("user", userPermissions.user().username());
		assertFalse(userPermissions.isAdministrator());
	}
	@Test
	public void testAdminUser() {
		UserPermissions newUserPermissions = new UserPermissions(createUser("user"), true);
		userPermissionsJson.permissions().add(newUserPermissions);
		IUserWithPermissions userPermissions = tested.getUserPermissions(createUser("user"));
		assertTrue(userPermissions.isAdministrator());
	}

}
