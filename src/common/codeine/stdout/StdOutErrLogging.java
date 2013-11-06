package codeine.stdout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * An OutputStream that writes contents to a Logger upon each call to flush()
 */
public class StdOutErrLogging extends ByteArrayOutputStream {

	private static final Logger log = Logger.getLogger(StdOutErrLogging.class);

	private String lineSeparator;
	private LogLevel logLevel;

	public enum LogLevel {
		StdOut, StdErr;
	}

	/**
	 * Constructor
	 * 
	 * @param logger
	 *            Logger to write to
	 * @param level
	 *            Level at which to write the log message
	 */
	public StdOutErrLogging(LogLevel logLevel) {
		super();
		this.logLevel = logLevel;
		lineSeparator = System.getProperty("line.separator");
	}

	/**
	 * upon flush() write the existing contents of the OutputStream to the
	 * logger as a log record.
	 * 
	 * @throws java.io.IOException
	 *             in case of error
	 */
	@Override
	public void flush() throws IOException {

		String record;
		synchronized (this) {
			super.flush();
			record = this.toString();
			super.reset();

			if (record.length() == 0 || record.equals(lineSeparator)) {
				// avoid empty records
				return;
			}

			switch (logLevel) {
			case StdErr: {
				log.warn(record);
				break;
			}
			case StdOut: {
				log.info(record);
				break;
			}
			default:
				throw new IllegalArgumentException("level is not defined: " + logLevel);
			}
		}
	}
}