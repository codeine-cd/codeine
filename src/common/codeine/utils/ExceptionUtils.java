package codeine.utils;

import java.io.PrintWriter;
import java.io.StringWriter;


public class ExceptionUtils
{
	
	public static RuntimeException asUnchecked(Throwable t)
	{
		if (t instanceof RuntimeException)
		{
			return (RuntimeException)t;
		}
		else if (t instanceof Error)
		{
			throw (Error)t;
		}
		else
		{
			return new RuntimeException("wrapped exception - check cause for details, message from cause:" + t.getMessage(), t);
		}
	}

	public static Throwable getRootCause(Throwable t) {
		Throwable $ = t;
		while ($.getCause() != null && $.getCause() != $) {
			$ = $.getCause();
		}
		return $;
	}

	public static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return sw.toString(); // stack trace as a string
	}
	
}
