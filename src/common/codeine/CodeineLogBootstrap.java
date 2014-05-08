package codeine;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import codeine.model.Constants;
import codeine.utils.FilesUtils;

public class CodeineLogBootstrap {

	public void init(String componentName, String logFileName) {
		String logfile = Constants.getLogDir() + "/" + logFileName;
		setLogger(logfile);
		String startupMessage = "\n\n========================>>>   Starting codeine " + componentName + " at version " + CodeineVersion.get() + "    <<<========================\n\n";
		System.out.println(startupMessage);
		Logger.getRootLogger().info(startupMessage);
	}

	private void setLogger(String logfile) {
		if (logToStdout()) {
			System.out.println("logging to std-out");
			BasicConfigurator.configure();
			Logger.getRootLogger().setLevel(Level.INFO);
			return;
		}
		FilesUtils.mkdirs(Constants.getLogDir());
		System.out.println("writing log to " + logfile);
		String pattern = "%d{ISO8601} [%t] [%c] %p %m %n";
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
