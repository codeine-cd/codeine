package codeine.command_peer;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class TimeToSleepCalculatorTest {

	TimeToSleepCalculator timeToSleepCalculator = new TimeToSleepCalculator();
	
	@Test
	public void testRatioMoreThan1() {
		assertEquals(TimeUnit.SECONDS.toMillis(40), timeToSleepCalculator.getTimeToSleep(2, TimeUnit.SECONDS.toMillis(20)));
	}
	@Test
	public void testRatioLessThan1() {
		assertEquals(TimeUnit.SECONDS.toMillis(90), timeToSleepCalculator.getTimeToSleep(0.5, TimeUnit.SECONDS.toMillis(30)));
	}
	@Test
	public void testRatio1() {
		assertEquals(TimeUnit.SECONDS.toMillis(30), timeToSleepCalculator.getTimeToSleep(1, TimeUnit.SECONDS.toMillis(30)));
	}

}
