package codeine.command_peer;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class LinearProgressiveRateClaculatorTest {

	private LinearProgressiveRateClaculator ratioCalculator = new LinearProgressiveRateClaculator();
			
	@Test
	public void testNoMoreMinutesLeft() {
		ratioCalculator.iterationStart(0, 5);
		assertEquals(5, ratioCalculator.numOfNodesToExecute());
		assertEquals(5, ratioCalculator.ratio(), 0.1);
	}
	@Test
	public void testRatio() {
		ratioCalculator.iterationStart(2, 22);
		assertEquals(11, ratioCalculator.numOfNodesToExecute());
		assertEquals(11, ratioCalculator.ratio(), 0.1);
	}
	@Test
	public void testRatioNotInt() {
		ratioCalculator.iterationStart(3, 22);
		assertEquals(8, ratioCalculator.numOfNodesToExecute());
		assertEquals(22/(double)3, ratioCalculator.ratio(), 0.1);
	}

	@Test
	public void testRatioMoreThan1() {
		assertEquals(TimeUnit.SECONDS.toMillis(40), ratioCalculator.getTimeToSleep(2, TimeUnit.SECONDS.toMillis(20)));
	}
	@Test
	public void testRatioLessThan1() {
		assertEquals(TimeUnit.SECONDS.toMillis(90), ratioCalculator.getTimeToSleep(0.5, TimeUnit.SECONDS.toMillis(30)));
	}
	@Test
	public void testRatio1() {
		assertEquals(TimeUnit.SECONDS.toMillis(30), ratioCalculator.getTimeToSleep(1, TimeUnit.SECONDS.toMillis(30)));
	}
}
