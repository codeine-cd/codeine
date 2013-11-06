package codeine.utils;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.base.Function;

public class StringUtils {

	public static String formatTimePeriod(long millis) {
		long seconds = (millis / 1000) % 60;
		long minutes = (millis / (1000 * 60)) % 60;
		long hours = (millis / (1000 * 60 * 60));// %24;
		long onlyMillis = millis % 1000;
		boolean hasMillis = onlyMillis > 0;
		if (hours > 0){
			String format = "%d:%02d:%02d";
			if (hasMillis){
				format += ".%03d";
			}
			else {
				format += ".0";
			}
			return String.format(format, hours, minutes, seconds, onlyMillis);
		}
		else if (minutes > 0){
			String format = "%d:%02d";
			if (hasMillis){
				format += ".%03d";
			}
			else {
				format += ".0";
			}
			return String.format(format, minutes, seconds, onlyMillis);
		}
		else if (seconds > 0){
			String format = "%d";
			if (hasMillis){
				format += ".%03d seconds";
			}
			else {
				format += ".0 seconds";
			}
			return String.format(format, seconds, onlyMillis);
		}
		else {
			return String.format("%d milliseconds", onlyMillis);
		}
		
	}

	public static String[] getEnumNames(Class<? extends Enum<?>> e) {
	    return Arrays.toString(e.getEnumConstants()).replaceAll("\\[|]", "").split(", ");
	}
	
	public static boolean isEmpty(String string) {
		return null == string || "".equals(string.trim());
	}

	public static String safeToString(Object obj) {
		if (null == obj){
			return "";
		}
		return obj.toString();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String collectionToString(Collection<?> c){
		Function predicate = new Function<Object, String>() {
			@Override
			public String apply(Object input) {
				return input.toString();
			}
		};
		return collectionToString(c, predicate);
	}
	public static <T> String collectionToString(Collection<T> c, Function<T, String> function){
		String $ = "";
		boolean first = true;
		for (T object : c) {
			if (first){
				first = false;
			}
			else {
				$ += " ";
			}
			$ += function.apply(object);
		}
		return $;
	}
}
