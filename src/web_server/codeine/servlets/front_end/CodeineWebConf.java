package codeine.servlets.front_end;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

public class CodeineWebConf {
	private Map<String, List<String>> views = Maps.newHashMap();

	public Map<String, List<String>> views() {
		return views;
	}
}
