package yami;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import org.junit.*;

import yami.configuration.*;
import yami.mail.*;
import yami.model.*;

public class UpdaterThreadTest
{
	
	private ForTestingMailSender yamiMailSender;
	private DataStore d;
	private List<Node> a;
	private List<HttpCollector> c;
	
	private final class ForTestingCollectorHttpResultFetcher extends CollectorHttpResultFetcher
	{
		@Override
		public Result getResult(HttpCollector c, Node n) throws IOException, InterruptedException
		{
			return new Result(0, "result output");
		}
	}
	
	private final class ForTestingFailingCollectorHttpResultFetcher extends CollectorHttpResultFetcher
	{
		@Override
		public Result getResult(HttpCollector c, Node n) throws IOException, InterruptedException
		{
			return new Result(1, "result output");
		}
	}
	
	private final class ForTestingMailSender extends YamiMailSender
	{
		
		private List<Call> callHistory = new ArrayList<Call>();
		
		public ForTestingMailSender()
		{
			super(null);
		}
		
		public void sendMailIfNeeded(IDataStore d, HttpCollector c, Node n, CollectorOnAppState state)
		{
			add(new Call(c, n, state));
		}
		
		private void add(Call call)
		{
			callHistory.add(call);
		}
		
	}
	
	private final class ForTestingDataStore extends DataStore
	{
		private final List<HttpCollector> collectors;
		private final List<Node> apps;
		
		public ForTestingDataStore(List<HttpCollector> collectors, List<Node> apps)
		{
			super();
			this.collectors = collectors;
			this.apps = apps;
		}
		
		@Override
		public List<HttpCollector> collectors()
		{
			return collectors;
		}
		
		@Override
		public List<Node> appInstances()
		{
			return apps;
		}
		
	}
	
	private Node addNode(DataStore d, String string, boolean addToDataStore)
	{
		Node node = new Node();
		node.name = string;
		if (addToDataStore)
		{
			d.appInstances().add(node);
		}
		return node;
	}
	
	private HttpCollector addCollector(DataStore d, String name, boolean addToDatastore)
	{
		HttpCollector c = createHttpCollector();
		c.name = name;
		c.includedNode.add("all");
		if (addToDatastore)
		{
			d.collectors().add(c);
		}
		return c;
	}
	
	@Test
	public void testUpdateResultsEmpty()
	{
		List<HttpCollector> c = new ArrayList<HttpCollector>();
		List<Node> a = new ArrayList<Node>();
		DataStore d = new ForTestingDataStore(c, a);
		
		ForTestingMailSender yamiMailSender = new ForTestingMailSender();
		UpdaterThread tested = new UpdaterThread(yamiMailSender, null);
		tested.updateResults(d);
		assertTrue(yamiMailSender.callHistory.isEmpty());
		assertTrue(d.resultsByMonitoredApp.isEmpty());
	}
	
	@Test
	public void testUpdateResults_AddsOneResultForOneNodeAndOneCollector()
	{
		
		List<HttpCollector> c = new ArrayList<HttpCollector>();
		List<Node> a = new ArrayList<Node>();
		DataStore d = new ForTestingDataStore(c, a);
		HttpCollector hc = createHttpCollector();
		hc.name = "test";
		hc.includedNode.add("all");
		d.collectors().add(hc);
		addNode(d, "node1", true);
		
		CollectorHttpResultFetcher fetcher = new ForTestingCollectorHttpResultFetcher();
		
		ForTestingMailSender ms = new ForTestingMailSender();
		UpdaterThread tested = new UpdaterThread(ms, fetcher);
		tested.updateResults(d);
		assertEquals(1, ms.callHistory.size());
		assertFalse(d.resultsByMonitoredApp.isEmpty());
	}
	
	@Test
	public void testUpdateResults_AddsFourResultsForTwoCollectorsAndTwoNodes()
	{
		List<HttpCollector> c = new ArrayList<HttpCollector>();
		List<Node> a = new ArrayList<Node>();
		
		DataStore d = new ForTestingDataStore(c, a);
		addCollector(d, "collector1", true);
		addCollector(d, "collector2", true);
		
		addNode(d, "node1", true);
		addNode(d, "node2", true);
		CollectorHttpResultFetcher fetcher = new ForTestingCollectorHttpResultFetcher();
		
		ForTestingMailSender yamiMailSender = new ForTestingMailSender();
		UpdaterThread tested = new UpdaterThread(yamiMailSender, fetcher);
		
		tested.updateResults(d);
		assertEquals(2, d.resultsByMonitoredApp.size());
		assertEquals(4, yamiMailSender.callHistory.size());
		for (Entry<Node, Map<HttpCollector, CollectorOnAppState>> e : d.resultsByMonitoredApp.entrySet())
		{
			assertEquals(2, e.getValue().size());
		}
	}
	
	@Test
	public void testUpdateResults_ResultForNotIncludedNodes()
	{
		List<HttpCollector> c = new ArrayList<HttpCollector>();
		List<Node> a = new ArrayList<Node>();
		DataStore d = new ForTestingDataStore(c, a);
		CollectorHttpResultFetcher fetcher = new ForTestingCollectorHttpResultFetcher();
		ForTestingMailSender yamiMailSender = new ForTestingMailSender();
		UpdaterThread tested = new UpdaterThread(yamiMailSender, fetcher);
		
		addNode(d, "node1", true);
		
		HttpCollector hc = addCollector(d, "collector1", true);
		
		hc.includedNode.clear();
		tested.updateResults(d);
		
		assertEquals(0, d.resultsByMonitoredApp.size());
		assertEquals(0, yamiMailSender.callHistory.size());
	}
	
	@Test
	public void testUpdateResults_ResultForIncludedNode()
	{
		List<HttpCollector> c = new ArrayList<HttpCollector>();
		List<Node> a = new ArrayList<Node>();
		DataStore d = new ForTestingDataStore(c, a);
		CollectorHttpResultFetcher fetcher = new ForTestingCollectorHttpResultFetcher();
		ForTestingMailSender yamiMailSender = new ForTestingMailSender();
		UpdaterThread tested = new UpdaterThread(yamiMailSender, fetcher);
		
		addNode(d, "node1", true);
		
		HttpCollector hc = addCollector(d, "collector1", true);
		
		hc.includedNode.clear();
		hc.includedNode.add("node1");
		tested.updateResults(d);
		
		assertEquals(1, d.resultsByMonitoredApp.size());
		assertEquals(1, yamiMailSender.callHistory.size());
	}
	
	@Test
	public void testUpdateResults_ResultForExcludedNodeWithALLIncluded()
	{
		c = new ArrayList<HttpCollector>();
		a = new ArrayList<Node>();
		d = new ForTestingDataStore(c, a);
		CollectorHttpResultFetcher fetcher = new ForTestingCollectorHttpResultFetcher();
		yamiMailSender = new ForTestingMailSender();
		UpdaterThread tested = new UpdaterThread(yamiMailSender, fetcher);
		addNode(d, "node1", true);
		HttpCollector hc = addCollector(d, "collector1", true);
		
		hc.includedNode.add("all");
		hc.excludedNode.add("node1");
		tested.updateResults(d);
		
		assertEquals(0, d.resultsByMonitoredApp.size());
		assertEquals(0, yamiMailSender.callHistory.size());
		
		addNode(d, "node2", true);
		tested.updateResults(d);
		
		assertEquals(1, d.resultsByMonitoredApp.size());
		assertEquals(1, yamiMailSender.callHistory.size());
	}
	
	@Test
	public void testMonitorDependencyPreventSendingMail() throws Exception
	{
		List<HttpCollector> c = new ArrayList<HttpCollector>();
		List<Node> a = new ArrayList<Node>();
		DataStore d = new ForTestingDataStore(c, a);
		
		HttpCollector master = createHttpCollector();
		
		master.name = "master";
		master.includedNode.add("all");
		
		HttpCollector hc = createHttpCollector();
		hc.name = "slave";
		hc.includedNode.add("all");
		hc.dependsOn().add(master);
		d.collectors().add(hc);
		
		HttpCollector hc2 = createHttpCollector();
		hc2.name = "bulgaria";
		hc2.includedNode.add("all");
		hc2.dependsOn().add(master);
		d.collectors().add(hc2);
		
		addNode(d, "node1", true);
		
		CollectorHttpResultFetcher fetcher = new ForTestingCollectorHttpResultFetcher();
		
		ForTestingMailSender ms = new ForTestingMailSender();
		UpdaterThread tested = new UpdaterThread(ms, fetcher);
		tested.updateResults(d);
		assertEquals(0, ms.callHistory.size());
	}
	
	@Test
	public void testMonitorDependencyMasterOKShouldMail() throws Exception
	{
		List<HttpCollector> c = new ArrayList<HttpCollector>();
		List<Node> a = new ArrayList<Node>();
		DataStore d = new ForTestingDataStore(c, a);
		HttpCollector master = createHttpCollector();
		HttpCollector hc = createHttpCollector();
		
		master.name = "master";
		master.includedNode.add("all");
		d.collectors().add(master);
		
		hc.name = "slave";
		hc.includedNode.add("all");
		hc.dependsOn().add(master);
		d.collectors().add(hc);
		
		addNode(d, "node1", true);
		
		CollectorHttpResultFetcher fetcher = new ForTestingCollectorHttpResultFetcher();
		
		ForTestingMailSender ms = new ForTestingMailSender();
		UpdaterThread tested = new UpdaterThread(ms, fetcher);
		tested.updateResults(d);
		assertEquals(2, ms.callHistory.size());
	}
	
	@Test
	public void testMonitorDependencyMasterFailedPreventSendingMail() throws Exception
	{
		List<HttpCollector> c = new ArrayList<HttpCollector>();
		List<Node> a = new ArrayList<Node>();
		DataStore d = new ForTestingDataStore(c, a);
		
		HttpCollector master = createHttpCollector();
		
		master.name = "master";
		master.includedNode.add("all");
		d.collectors().add(master);
		
		HttpCollector hc = createHttpCollector();
		hc.name = "slave";
		hc.includedNode.add("all");
		hc.dependsOn().add(master);
		d.collectors().add(hc);
		
		HttpCollector hc2 = createHttpCollector();
		hc2.name = "bulgaria";
		hc2.includedNode.add("all");
		hc2.dependsOn().add(master);
		d.collectors().add(hc2);
		
		addNode(d, "node1", true);
		
		CollectorHttpResultFetcher fetcher = new ForTestingFailingCollectorHttpResultFetcher();
		
		ForTestingMailSender ms = new ForTestingMailSender();
		UpdaterThread tested = new UpdaterThread(ms, fetcher);
		tested.updateResults(d);
		assertEquals(1, ms.callHistory.size());
	}
	
	private HttpCollector createHttpCollector()
	{
		return new HttpCollector()
		{
			private List<HttpCollector> l = new ArrayList<HttpCollector>();
			
			@Override
			public List<HttpCollector> dependsOn()
			{
				return l;
			}
		};
	}
	
}
