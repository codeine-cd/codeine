package codeine.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class LimitedQueueTest
{
	
	@Test(expected = IllegalArgumentException.class)
	public void testLimitedQueue_IllegalArgument()
	{
		new LimitedQueue<String>(-1);
	}
	
	@Test
	public void testLimitedQueue_EmptyQueue()
	{
		LimitedQueue<String> q = new LimitedQueue<String>(1);
		assertTrue(q.isEmpty());
	}
	
	@Test
	public void testAddE()
	{
		LimitedQueue<String> q = new LimitedQueue<String>(3);
		q.add("line1");
		q.add("line2");
		assertEquals(2, q.size());
		q.add("line3");
		q.add("line4");
		assertEquals(3, q.size());
	}
	
}
