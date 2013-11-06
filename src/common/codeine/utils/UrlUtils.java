package codeine.utils;

import java.util.Map;

public class UrlUtils {

	
	public static String buildUrl(String uri, Map<String, String> queryParams) {
		StringBuilder $ = new StringBuilder(uri);
		if (queryParams != null && queryParams.size() > 0) {
			$.append("?");
			for (String parameter : queryParams.keySet()) {
				$.append(parameter + "=" + queryParams.get(parameter) + "&");
			}
			$.deleteCharAt($.length()-1);
		}
		return $.toString();
	}
}
