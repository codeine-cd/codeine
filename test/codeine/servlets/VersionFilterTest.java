package codeine.servlets;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import codeine.model.Constants;
import codeine.version.ViewNodesFilter;

public class VersionFilterTest {

	@Test
	public void testFilterByVersion() {
		ViewNodesFilter tested = new ViewNodesFilter(Constants.NO_VERSION, 2, null, 0);
		assertTrue(tested.filter("a", null));
		assertFalse(tested.filter(null, null));
		assertFalse(tested.filter(null, null));
		assertTrue(tested.filter(null, null));
	}
	@Test
	public void testSkip() {
		ViewNodesFilter tested = new ViewNodesFilter(Constants.NO_VERSION, 2, null, 1);
		assertTrue(tested.filter(null, null));
		assertFalse(tested.filter(null, null));
		assertFalse(tested.filter(null, null));
		assertTrue(tested.filter(null, null));
	}
	@Test
	public void testFilterByVersionAll() {
		ViewNodesFilter tested = new ViewNodesFilter(Constants.ALL_VERSION, 1, null, 0);
		assertFalse(tested.filter(null, null));
	}

	@Test
	public void testFilterByRegexp() {
		ViewNodesFilter tested = new ViewNodesFilter(Constants.NO_VERSION, Integer.MAX_VALUE, "aba", 0);
		assertFalse(tested.filter(null, "aba"));
		assertFalse(tested.filter(null, "aabaa"));
		assertTrue(tested.filter(null, "a"));
	}
	@Test
	public void testFilterByRegexp2() {
		ViewNodesFilter tested = new ViewNodesFilter(Constants.NO_VERSION, Integer.MAX_VALUE, ".*", 0);
		assertFalse(tested.filter(null, "aba"));
		assertFalse(tested.filter(null, null));
	}
}
