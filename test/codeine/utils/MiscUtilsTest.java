package codeine.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class MiscUtilsTest {

	@Test
	public void testNull() {
		assertFalse(MiscUtils.equals(null, null));
	}
	@Test
	public void testEquals() {
		assertTrue(MiscUtils.equals("a", "a"));
	}

}
