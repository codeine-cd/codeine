package codeine.utils;

import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

public class MapUtils {

	private static final Logger log = Logger.getLogger(MapUtils.class);
	
	public static Map<String,String> noNullsMap(Map<String,String> map) {
		if (!map.containsKey(null) && !map.containsValue(null)) {
			return map;
		}
		Function<String, String> function = new Function<String, String>() {
			@Override
			public String apply(String input) {
				return StringUtils.safeToString(input);
			}
		};
		Map<String, String> $ = Maps.newHashMap(Maps.transformValues(map, function));
		if ($.containsKey(null)) {
			if ($.containsKey("")) {
				throw new IllegalArgumentException("map contains both null and empty string keys " + map);
			} else {
				log.debug("map contains null key with value " + $.get(null));
				$.put("", $.remove(null));
			}
		}
		return $;
	}
}
