package codeine.command_peer;

public class RatioCalculator {
	
	private static final int MAX_CONCERENCY = 100;
	private double minutesLeft;
	private int totalNodes,concerency;
	private double ratio;

	public RatioCalculator(double minutesLeft, int totalNodes) {
		this.minutesLeft = minutesLeft;
		this.totalNodes = totalNodes;
		calc();
	}
	
	private void calc() {
		minutesLeft = minutesLeft < 1 ? 1 : minutesLeft;
		ratio = totalNodes / minutesLeft;
		concerency = (int) Math.ceil(ratio);
		concerency = Math.min(concerency, MAX_CONCERENCY);
	}
	
	public double ratio() {
		return ratio;
	}
	
	
	public int concerency() {
		return concerency;
	}
}
