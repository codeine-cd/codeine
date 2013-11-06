package codeine.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringUtilsTest {

	@Test
	public void testFormatTimePeriod() {
		assertEquals("15 milliseconds", StringUtils.formatTimePeriod(15));
		assertEquals("25:23.456", StringUtils.formatTimePeriod(1523456));
		assertEquals("25:23.0", StringUtils.formatTimePeriod(1523000));
		assertEquals("55.0 seconds", StringUtils.formatTimePeriod(55000));
	}

}
