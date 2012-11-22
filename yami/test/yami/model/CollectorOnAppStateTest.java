package yami.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import yami.mail.CollectorOnAppState;

public class CollectorOnAppStateTest
{
	
	@Test
	public void testPrevStateWhenEmpty()
	{
		CollectorOnAppState tested = new CollectorOnAppState();
		assertTrue(tested.prevState());
	}
	@Test
	public void testStateWhenOneSuccess()
	{
		CollectorOnAppState tested = new CollectorOnAppState();
		addResult(tested, 0);
		assertTrue(tested.prevState());
		assertTrue(tested.state());
	}
	@Test
	public void testStateWhen2Success1Failure()
	{
		CollectorOnAppState tested = new CollectorOnAppState();
		addResult(tested, 0);
		addResult(tested, 2);
		addResult(tested, 0);
		assertFalse(tested.prevState());
		assertTrue(tested.state());
	}
	@Test
	public void testStateWhen1Success1Failure()
	{
		CollectorOnAppState tested = new CollectorOnAppState();
		addResult(tested, 0);
		addResult(tested, 2);
		assertFalse(tested.state());
	}
	public void addResult(CollectorOnAppState tested, int exitStatus)
	{
		Result r = new Result(exitStatus, null);
		tested.addResult(r );
	}
	@Test
	public void testStateWhenOneFailure()
	{
		CollectorOnAppState tested = new CollectorOnAppState();
		Result r = new Result(1, null);
		tested.addResult(r );
		assertFalse(tested.state());
	}
}
