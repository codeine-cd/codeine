package codeine.command_peer;

import java.util.concurrent.TimeUnit;


public class TimeToSleepCalculator {

	public long getTimeToSleep(double ratio, long loopTime) {
		long sleepTime = 0;
		if (ratio > 1) {
			 sleepTime = TimeUnit.MINUTES.toMillis(1) - loopTime;
		} else {
			sleepTime = TimeUnit.MINUTES.toMillis((long) Math.ceil(1 / ratio)) - loopTime;
		}
		return sleepTime;
	}
}
