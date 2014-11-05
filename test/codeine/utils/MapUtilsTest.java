package codeine.utils;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Test;

import com.google.common.collect.Maps;

public class MapUtilsTest {

	@Test
	public void test_nullsToEmptyStringMap_empty() {
		assertEquals(createMap(), MapUtils.noNullsMap(createMap()));
	}
	@Test
	public void test_nullsToEmptyStringMap_WithValue() {
		HashMap<String, String> map = createMap("a", "b");
		assertEquals(map, MapUtils.noNullsMap(map));
	}
	private HashMap<String, String> createMap(String key, String value) {
		HashMap<String, String> map = createMap();
		map.put(key, value);
		return map;
	}
	@Test
	public void test_nullsToEmptyStringMap_nullValue() {
		assertEquals(createMap("a", ""), MapUtils.noNullsMap(createMap("a", null)));
	}
	@Test
	public void test_nullsToEmptyStringMap_nullKey() {
		assertEquals(createMap("", "a"), MapUtils.noNullsMap(createMap(null, "a")));
	}
	@Test
	public void test_nullsToEmptyStringMap_nullKeyValue() {
		assertEquals(createMap("", ""), MapUtils.noNullsMap(createMap(null, null)));
	}
	@Test(expected=IllegalArgumentException.class)
	public void test_nullsToEmptyStringMap_nullAndEmptyKey() {
		HashMap<String, String> map = createMap(null, null);
		map.put("", "A");
		MapUtils.noNullsMap(map);
	}

	private HashMap<String, String> createMap() {
		return Maps.<String, String>newHashMap();
	}

}
