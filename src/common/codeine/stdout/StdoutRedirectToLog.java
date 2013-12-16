package codeine.stdout;

import java.io.PrintStream;

import codeine.stdout.StdOutErrLogging.LogLevel;

public class StdoutRedirectToLog {

	private static PrintStream stdout;
	private static PrintStream stderr;

	public static void redirect() {
		// preserve old stdout/stderr streams in case they might be useful
		stdout = System.out;
		stderr = System.err;

		// now rebind stdout/stderr to logger
		System.setOut(new PrintStream(new StdOutErrLogging(LogLevel.StdOut), true));
		System.setErr(new PrintStream(new StdOutErrLogging(LogLevel.StdErr), true));
	}
}
