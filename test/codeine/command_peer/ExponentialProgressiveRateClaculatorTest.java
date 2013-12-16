package codeine.command_peer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ExponentialProgressiveRateClaculatorTest {

	private ExponentialProgressiveRateClaculator tested = new ExponentialProgressiveRateClaculator(0, 0);
	
	@Test
	public void testNumberOfNodesInIteration() {
		tested.iterationStart(0, 10);
		assertEquals(1, tested.numOfNodesToExecute());
		tested.iterationStart(0, 10);
		assertEquals(2, tested.numOfNodesToExecute());
		tested.iterationStart(0, 10);
		assertEquals(4, tested.numOfNodesToExecute());
	}
	@Test
	public void testNotRunningMoreThanExistingNodesInIteration() {
		tested.iterationStart(0, 10);
		assertEquals(1, tested.numOfNodesToExecute());
		tested.iterationStart(0, 1);
		assertEquals(1, tested.numOfNodesToExecute());
	}
	@Test
	public void testSimple() {
		tested = new ExponentialProgressiveRateClaculator(7, 9);
		assertEquals(3, tested.getTimeToSleep(0));
	}
	@Test
	public void testZero() {
		tested = new ExponentialProgressiveRateClaculator(0, 9);
		assertEquals(0, tested.getTimeToSleep(0));
	}
	@Test
	public void testNotDivided() {
		tested = new ExponentialProgressiveRateClaculator(4, 9);
		assertEquals(3, tested.getTimeToSleep(0));
	}
	@Test
	public void testNotDivided2() {
		tested = new ExponentialProgressiveRateClaculator(4, 10);
		assertEquals(3, tested.getTimeToSleep(0));
	}
	@Test
	public void testManyNodes() {
		tested = new ExponentialProgressiveRateClaculator(1000, 1);
		assertEquals(0, tested.getTimeToSleep(0));
	}
	@Test
	public void testSimpleWithExecution() {
		tested = new ExponentialProgressiveRateClaculator(7, 9);
		assertEquals(1, tested.getTimeToSleep(2));
	}
	@Test
	public void testSimpNoNegativeSleepTime() {
		tested = new ExponentialProgressiveRateClaculator(7, 9);
		assertEquals(0, tested.getTimeToSleep(4));
	}
}
