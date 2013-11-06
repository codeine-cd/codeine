package codeine.utils;

public class MongoUtils {

	public static String decode(String value) {
		return value.replace("%2E", ".");
	}

	public static String encode(String value) {
		return value.replace(".", "%2E");
	}

}
