package codeine.permissions;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.collect.Sets;

public class UserProjectPermissionsTest {

	@Test
	public void testCanCommandWhenCanConfigure() {
		UserProjectPermissions tested = new UserProjectPermissions("username", true, Sets.<String>newHashSet(), false);
		assertTrue(tested.canConfigure());
		assertTrue(tested.canCommand());
		assertTrue(tested.canCommand("node"));
		assertTrue(tested.canRead());
	}

}
