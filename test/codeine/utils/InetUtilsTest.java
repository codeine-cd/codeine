package codeine.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import codeine.utils.network.InetUtils;

public class InetUtilsTest {

	@Test
	public void testDomain() {
		assertEquals("", InetUtils.domain("a"));
		assertEquals("b.b", InetUtils.domain("a.b.b"));
		assertEquals("b.b", InetUtils.domain("a.b.b:8080"));
	}
	@Test
	public void testNameWithoutPort() {
		assertEquals("a.b.b", InetUtils.nameWithoutPort("a.b.b"));
		assertEquals("a.b.b", InetUtils.nameWithoutPort("a.b.b:8080"));
	}

}
