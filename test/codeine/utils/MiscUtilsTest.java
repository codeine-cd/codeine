package codeine.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class MiscUtilsTest {

	@Test
	public void testNull() {
		assertTrue(MiscUtils.equals(null, null));
	}
	@Test
	public void testEquals() {
		assertTrue(MiscUtils.equals("a", "a"));
	}
	@Test
	public void testNotEquals() {
		assertFalse(MiscUtils.equals("a", null));
	}
	@Test
	public void testNotEquals2() {
		assertFalse(MiscUtils.equals("a", "b"));
	}

}
