package codeine.utils.network;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UserAgentHeaderTest {

	@Test
	public void testNull() {
		assertEquals(UserAgentHeader.NULL, UserAgentHeader.parseBrowserAndOs((String)null));
	}
	@Test
	public void testIE11() {
		UserAgentHeader tested = UserAgentHeader.parseBrowserAndOs("Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko");
		assertEquals("IE-11.0", tested.getBrowser());
		assertEquals("Windows", tested.getOs());
	}

}
