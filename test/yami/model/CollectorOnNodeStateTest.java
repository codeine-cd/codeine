package yami.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import yami.mail.CollectorOnNodeState;

public class CollectorOnNodeStateTest
{
	
	@Test
	public void testPrevStateWhenEmpty()
	{
		CollectorOnNodeState tested = new CollectorOnNodeState();
		assertTrue(tested.prevState());
	}
	@Test
	public void testStateWhenOneSuccess()
	{
		CollectorOnNodeState tested = new CollectorOnNodeState();
		addResult(tested, 0);
		assertTrue(tested.prevState());
		assertTrue(tested.state());
	}
	@Test
	public void testStateWhen2Success1Failure()
	{
		CollectorOnNodeState tested = new CollectorOnNodeState();
		addResult(tested, 0);
		addResult(tested, 2);
		addResult(tested, 0);
		assertFalse(tested.prevState());
		assertTrue(tested.state());
	}
	@Test
	public void testStateWhen1Success1Failure()
	{
		CollectorOnNodeState tested = new CollectorOnNodeState();
		addResult(tested, 0);
		addResult(tested, 2);
		assertFalse(tested.state());
	}
	public void addResult(CollectorOnNodeState tested, int exitStatus)
	{
		Result r = new Result(exitStatus, null);
		tested.addResult(r );
	}
	@Test
	public void testStateWhenOneFailure()
	{
		CollectorOnNodeState tested = new CollectorOnNodeState();
		Result r = new Result(1, null);
		tested.addResult(r );
		assertFalse(tested.state());
	}
}
