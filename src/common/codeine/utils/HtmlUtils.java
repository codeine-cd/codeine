package codeine.utils;

public class HtmlUtils {

	public static String encodeHtmlElementId(String id) {
		return "ID_" + id.replaceAll("[^a-zA-Z0-9-_]", "");
	}

}
