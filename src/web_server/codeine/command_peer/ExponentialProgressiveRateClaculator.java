package codeine.command_peer;

public class ExponentialProgressiveRateClaculator implements ProgressiveRateClaculator {

	private int howMuchToRun = 1;
	private int iteration = 0;
	private long timeToSleep = 0;
	
	public ExponentialProgressiveRateClaculator(int totalNumOfNodes, long durationMilli) {
		int timeSlices  = (int) (Math.ceil(Math.log(totalNumOfNodes)) + 1);
		if (timeSlices == 0) {
			throw new IllegalArgumentException("will divide by zero: " + totalNumOfNodes + " " + durationMilli);
		}
		if (totalNumOfNodes > 0) {
			this.timeToSleep = durationMilli / timeSlices;
		}
		//else it should not sleep
	}

	@Override
	public void iterationStart(double minutesLeft, int nodesLeft) {
		howMuchToRun = Math.min((int) Math.pow(2, iteration), nodesLeft);
		iteration++;
	}

	@Override
	public int numOfNodesToExecute() {
		return howMuchToRun;
	}

	@Override
	public long getTimeToSleep(long iterationTime) {
		long actualSleepTime = timeToSleep - iterationTime;
		return actualSleepTime >= 0 ? actualSleepTime : 0;
	}

}
