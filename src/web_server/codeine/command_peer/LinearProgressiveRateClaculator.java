package codeine.command_peer;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class LinearProgressiveRateClaculator implements ProgressiveRateClaculator {

	private static final Logger log = Logger.getLogger(CommandExecutionStrategy.class);
	
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
		if (numOfNodesToExecute > CommandExecutionStrategy.MAX_NODES_TO_EXECUTE) {
			log.info("linear concurrency is above limit " + numOfNodesToExecute);
		}
		numOfNodesToExecute = Math.min(numOfNodesToExecute, CommandExecutionStrategy.MAX_NODES_TO_EXECUTE);
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
