package yami.utils;

import static org.junit.Assert.*;

import org.junit.Test;

import yami.utils.LimitedQueue;

public class LimitedQueueTest
{
	
	@Test(expected=IllegalArgumentException.class)
	public void testLimitedQueue_IllegalArgument()
	{
		@SuppressWarnings("unused")
		LimitedQueue<String> q = new LimitedQueue<String>(-1);
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
		q.add("line3");
		q.add("line4");
		assertEquals(3,q.size());
	}
	
}
