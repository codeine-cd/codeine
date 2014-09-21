package codeine.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class GzipUtilsTest {

	@Test
	public void testBasic() {
		String value = GzipUtils.decompress(GzipUtils.compress("test"));
		assertEquals("test", value);
	}

}
