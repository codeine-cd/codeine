package codeine.command_peer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RatioCalculatorTest {

	@Test
	public void testNoMoreMinutesLeft() {
		RatioCalculator ratioCalculator = new RatioCalculator(0, 5);
		assertEquals(5, ratioCalculator.concerency());
		assertEquals(5, ratioCalculator.ratio(), 0.1);
	}
	@Test
	public void testRatio() {
		RatioCalculator ratioCalculator = new RatioCalculator(2, 22);
		assertEquals(11, ratioCalculator.concerency());
		assertEquals(11, ratioCalculator.ratio(), 0.1);
	}
	@Test
	public void testRatioNotInt() {
		RatioCalculator ratioCalculator = new RatioCalculator(3, 22);
		assertEquals(8, ratioCalculator.concerency());
		assertEquals(22/(double)3, ratioCalculator.ratio(), 0.1);
	}

}
