package yami.model;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import yami.UpdaterThread;

public class testListToString
{
	
	@Test
	public void testSimpleDelimiter()
	{
		UpdaterThread tt = new UpdaterThread();
		String lineDelimiter = ",";
		List<String> list = new ArrayList<String>();
		list.add("aa");
		list.add("bb");
		list.add("cc");
		Assert.assertEquals("aa,bb,cc", tt.listToString(list, lineDelimiter));
		
	}
	
	@Test
	public void testEmptyList()
	{
		UpdaterThread tt = new UpdaterThread();
		String lineDelimiter = ",";
		List<String> list = new ArrayList<String>();
		Assert.assertEquals("", tt.listToString(list, lineDelimiter));
		
	}
	
	@Test
	public void testSingleElement()
	{
		UpdaterThread tt = new UpdaterThread();
		String lineDelimiter = "<br />";
		List<String> list = new ArrayList<String>();
		list.add("aa");
		Assert.assertEquals("aa", tt.listToString(list, lineDelimiter));
	}
	
	@Test
	public void testEmptyDelimiter()
	{
		UpdaterThread tt = new UpdaterThread();
		String lineDelimiter = "";
		List<String> list = new ArrayList<String>();
		list.add("aa");
		list.add("bb");
		list.add("cc");
		Assert.assertEquals("aabbcc", tt.listToString(list, lineDelimiter));
	}
	
	@Test
	public void testNullDelimiter()
	{
		UpdaterThread tt = new UpdaterThread();
		String lineDelimiter = null;
		List<String> list = new ArrayList<String>();
		list.add("aa");
		list.add("bb");
		list.add("cc");
		Assert.assertEquals("aabbcc", tt.listToString(list, lineDelimiter));
	}
	@Test
	public void testNullList()
	{
		UpdaterThread tt = new UpdaterThread();
		String lineDelimiter = "\n";
		List<String> list = null;
		Assert.assertEquals("", tt.listToString(list, lineDelimiter));
	}
	
}
