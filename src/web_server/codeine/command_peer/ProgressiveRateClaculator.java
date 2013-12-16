package codeine.command_peer;

public interface ProgressiveRateClaculator {

	void iterationStart(double minutesLeft, int nodesLeft);
	int numOfNodesToExecute();
	long getTimeToSleep(long loopTime);

}
