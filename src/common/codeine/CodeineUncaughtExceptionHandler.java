package codeine;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.log4j.Logger;

public class CodeineUncaughtExceptionHandler implements UncaughtExceptionHandler {
	
	private Logger log = Logger.getLogger(CodeineUncaughtExceptionHandler.class);

	public CodeineUncaughtExceptionHandler() {
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		try {
			log.error("Uncaught exception in thread " + t.getName(), e);
		} catch (Throwable tr) {
			tr.printStackTrace();
		}
	}

}