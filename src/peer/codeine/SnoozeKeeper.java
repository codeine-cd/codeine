package codeine;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class SnoozeKeeper {

	private static final Logger log = Logger.getLogger(SnoozeKeeper.class);
	private long globalSnooze = getSnoozeTime();

	public boolean isSnooze(String project, String node) {
		return System.currentTimeMillis() < globalSnooze;
	}

	public void snoozeAll() {
		log.info("setting globalSnooze");
		globalSnooze = getSnoozeTime();
	}

	private long getSnoozeTime() {
		return System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(2);
	}

}
