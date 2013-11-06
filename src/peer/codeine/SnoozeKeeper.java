package codeine;

import java.util.concurrent.TimeUnit;

public class SnoozeKeeper {

	private long globalSnooze = getSnoozeTime();

	public boolean isSnooze(String project, String node) {
		return System.currentTimeMillis() > globalSnooze;
	}

	public void snoozeAll() {
		globalSnooze = getSnoozeTime();
	}

	private static long getSnoozeTime() {
		return System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5);
	}

}
