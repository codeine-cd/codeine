package yami.model;

import java.util.*;

import junit.framework.Assert;

import org.junit.Test;

import yami.*;

public class testListToString
{
	
	@Test
	public void testSimpleDelimiter()
	{
		String lineDelimiter = ",";
		List<String> list = new ArrayList<String>();
		list.add("aa");
		list.add("bb");
		list.add("cc");
		Assert.assertEquals("aa,bb,cc", new ListJoiner().listToString(list, lineDelimiter));
		
	}
	
	@Test
	public void testEmptyList()
	{
		String lineDelimiter = ",";
		List<String> list = new ArrayList<String>();
		Assert.assertEquals("", new ListJoiner().listToString(list, lineDelimiter));
		
	}
	
	@Test
	public void testSingleElement()
	{
		String lineDelimiter = "<br />";
		List<String> list = new ArrayList<String>();
		list.add("aa");
		Assert.assertEquals("aa", new ListJoiner().listToString(list, lineDelimiter));
	}
	
	@Test
	public void testEmptyDelimiter()
	{
		String lineDelimiter = "";
		List<String> list = new ArrayList<String>();
		list.add("aa");
		list.add("bb");
		list.add("cc");
		Assert.assertEquals("aabbcc", new ListJoiner().listToString(list, lineDelimiter));
	}
	
	@Test
	public void testNullDelimiter()
	{
		String lineDelimiter = null;
		List<String> list = new ArrayList<String>();
		list.add("aa");
		list.add("bb");
		list.add("cc");
		Assert.assertEquals("aabbcc", new ListJoiner().listToString(list, lineDelimiter));
	}
	@Test
	public void testNullList()
	{
		String lineDelimiter = "\n";
		List<String> list = null;
		Assert.assertEquals("", new ListJoiner().listToString(list, lineDelimiter));
	}
	
}
