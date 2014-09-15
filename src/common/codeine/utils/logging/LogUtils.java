package codeine.utils.logging;

import org.apache.log4j.Logger;

public class LogUtils {

	public static void assertFailed(Logger log, Object... o) {
		StringBuilder sConcat = createLoggingString(new Exception(), o);
		AssertionError e = new AssertionError("Assertion failed:" + sConcat);
		log.error(sConcat.toString(), e);
	}

	public static void assertTrue(Logger log, boolean cond, Object... o) {
		if (cond) {
			return;
		}
		assertFailed(log, o);
	}

	private static StringBuilder createLoggingString(Throwable e, Object... objects) {
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
}
