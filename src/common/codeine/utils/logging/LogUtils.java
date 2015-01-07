package codeine.utils.logging;

import org.apache.log4j.Logger;

import codeine.utils.StringUtils;

public class LogUtils {

	public static void assertFailed(Logger log, Object... o) {
		String s = createLoggingString(o);
		AssertionError e = new AssertionError("Assertion failed: " + s);
		log.error(s, e);
	}

	public static void assertTrue(Logger log, boolean cond, Object... o) {
		if (cond) {
			return;
		}
		assertFailed(log, o);
	}

	@SuppressWarnings("unused")
	@Deprecated //method is slow - not for production
	private static StringBuilder createLoggingStringWithStack(Throwable e, Object... objects) {
		String caller = "Unkown";
		try {
			StackTraceElement ste[] = e.getStackTrace();
			caller = ste[1].getMethodName();
		} catch (final Throwable t) {
		}
		StringBuilder sConcat = new StringBuilder(caller + "() -");
		for (final Object o : objects) {
			sConcat.append(" " + o);
		}
		return sConcat;
	}
	private static String createLoggingString(Object... objects) {
		StringBuilder $ = new StringBuilder();
		for (final Object o : objects) {
			$.append(" " + o);
		}
		return $.toString();
	}

	public static void info(Logger log, Object... message) {
		log.info(StringUtils.arrayToString(message));
	}
}
