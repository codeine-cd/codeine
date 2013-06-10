package yami.servlets;

import static org.junit.Assert.*;

import org.junit.Test;

import yami.model.VersionResult;

public class VersionFilterTest
{
	
	@Test
	public void testFilterByVersion()
	{
		VersionFilter tested = new VersionFilter(VersionResult.NO_VERSION, 2);
		assertTrue(tested.filterByVersion("a"));
		assertFalse(tested.filterByVersion(null));
		assertFalse(tested.filterByVersion(null));
		assertTrue(tested.filterByVersion(null));
	}
	
}
