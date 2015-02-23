package codeine.utils.logging;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import codeine.utils.FilesUtils;

public class Log4jUtils {

	public static void initAppender(String logDir, String logFileName) {
		setLogger(logDir, logFileName);
	}

	private static void setLogger(String logDir, String logFileName) {
		String logfile = logDir + "/" + logFileName;
		if (logToStdout()) {
			BasicConfigurator.configure();
			Logger.getRootLogger().setLevel(Level.INFO);
			return;
		}
		FilesUtils.mkdirs(logDir);
		String pattern = "%d{ISO8601} [%t] [%c{1}] %p %m %n";
		PatternLayout layout = new PatternLayout(pattern);
		RollingFileAppender appender;
		try {
			appender = new RollingFileAppender(layout, logfile, true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		appender.setMaxBackupIndex(5);
		appender.setMaximumFileSize(10 * 1000000);
		Logger.getRootLogger().addAppender(appender);
		Logger.getRootLogger().setLevel(Level.INFO);
		if (System.getProperty("debug") != null && System.getProperty("debug").equals("true")) {
			Logger.getRootLogger().setLevel(Level.DEBUG);
		}
	}

	public static boolean logToStdout() {
		return Boolean.getBoolean("log.to.stdout");
	}
}
