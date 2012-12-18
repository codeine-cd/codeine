package yami.configuration;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;

import yami.model.*;

public class NodesTest
{
	
	@Test(expected = Exception.class)
	public void testEmptyNodes() throws Exception
	{
		DataStore a = new DataStore() { public java.util.List<Node> appInstances() {
			return new ArrayList<Node>();}; } ;
		assertTrue(Nodes.getNodes("host", a).isEmpty());
	}
	
	@Test()
	public void testNodeHasNode() throws Exception
	{
		DataStore a = new DataStore() { public java.util.List<Node> appInstances() {
			ArrayList<Node> arrayList = new ArrayList<Node>();
			Node e = new Node();
			e.node = new Peer("host");
			arrayList.add(e);
			return arrayList;}; } ;
		assertEquals(1,Nodes.getNodes("host", a).size());
	}
	
	
}
