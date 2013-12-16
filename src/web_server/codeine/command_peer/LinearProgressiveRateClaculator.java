package codeine.command_peer;

import java.util.concurrent.TimeUnit;

public class LinearProgressiveRateClaculator implements ProgressiveRateClaculator {

	private static final int MAX_CONCERENCY = 100;
	private double minutesLeft;
	private int nodesLeft,numOfNodesToExecute;
	private double ratio;

	@Override
	public void iterationStart(double minutesLeft, int nodesLeft) {
		this.minutesLeft = minutesLeft;
		this.nodesLeft = nodesLeft;
		calc();
	}
	
	private void calc() {
		minutesLeft = minutesLeft < 1 ? 1 : minutesLeft;
		ratio = nodesLeft / minutesLeft;
		numOfNodesToExecute = (int) Math.ceil(ratio);
		numOfNodesToExecute = Math.min(numOfNodesToExecute, MAX_CONCERENCY);
	}
	

	@Override
	public int numOfNodesToExecute() {
		return numOfNodesToExecute;
	}

	@Override
	public long getTimeToSleep(long loopTime) {
		return getTimeToSleep(ratio(), loopTime);
	}

	public double ratio() {
		return ratio;
	}

	public long getTimeToSleep(double ratioCalculated, long loopTime) {
		long sleepTime = 0;
		if (ratioCalculated > 1) {
			sleepTime = TimeUnit.MINUTES.toMillis(1) - loopTime;
		} else {
			sleepTime = TimeUnit.MINUTES.toMillis((long) Math.ceil(1 / ratioCalculated)) - loopTime;
		}
		return sleepTime;
	}

}
