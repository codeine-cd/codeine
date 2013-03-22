package yami.configuration;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import yami.model.DataStore;

public class NodesTest
{
	
	private static final String HOST = "host";

	@Test(expected = Exception.class)
	public void testEmptyNodes() throws Exception
	{
		DataStore a = new DataStore()
		{
			@Override
			public java.util.List<Node> appInstances()
			{
				return new ArrayList<Node>();
			}
		};
		assertTrue(Nodes.getNodes(HOST, a).isEmpty());
	}
	
	@Test()
	public void testNodeHasNode() throws Exception
	{
		DataStore a = new DataStore()
		{
			@Override
			public java.util.List<Node> appInstances()
			{
				ArrayList<Node> arrayList = new ArrayList<Node>();
				Node e = new Node();
				e.peer = new Peer();
				e.peer.name = HOST;
				arrayList.add(e);
				return arrayList;
			}
		};
		assertEquals(1, Nodes.getNodes(HOST, a).size());
	}
	
}
