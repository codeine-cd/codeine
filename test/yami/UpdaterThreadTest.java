package yami;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import yami.YamiMailSenderTest.ForTestingSendMailStrategy;
import yami.configuration.HttpCollector;
import yami.configuration.MailPolicy;
import yami.configuration.Node;
import yami.configuration.Peer;
import yami.mail.CollectorOnAppState;
import yami.model.DataStore;
import yami.model.IDataStore;
import yami.model.Result;

import com.google.common.collect.Lists;

public class UpdaterThreadTest
{
	
	private ForTestingMailSender yamiMailSender;
	private DataStore d;
	private UpdaterThread tested;
	
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
	
	private class ForTestingMailSender extends YamiMailSender
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
		
		protected void add(Call call)
		{
			callHistory.add(call);
		}
		
	}
	
	private final class ForTestingMailSenderWithDependencyAndPolicyValidation extends ForTestingMailSender
	{
		
		public void sendMailIfNeeded(IDataStore d, HttpCollector c, Node n, CollectorOnAppState state)
		{
			if (!shouldMail(c, n, d))
			{
				return;
			}
			
			if (!shouldMailByPolicies(d.mailingPolicy(), state))
			{
				return;
			}
			add(new Call(c, n, state));
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
		
		@Override
		public List<String> mailingList()
		{
			return new ArrayList<String>();
		}
		
		@Override
		public List<MailPolicy> mailingPolicy()
		{
			List<MailPolicy> l = Lists.newArrayList(MailPolicy.NewFailure);
			return l;
		}
		
	}
	
	private Node addNode(DataStore d, String string, boolean addToDataStore)
	{
		Node node = new Node();
		node.name = string;
		Peer peer = new Peer();
		peer.mailingList = Lists.newArrayList();
		node.peer = peer;
		if (addToDataStore)
		{
			d.appInstances().add(node);
		}
		return node;
	}
	
	private HttpCollector addCollector(DataStore d, String name, String included, boolean addToDatastore)
	{
		HttpCollector c = createHttpCollector(name, included);
		if (addToDatastore)
		{
			d.collectors().add(c);
		}
		return c;
	}
	
	private HttpCollector addCollector(DataStore d, String name, String included)
	{
		return addCollector(d, name, included, true);
	}
	
	private HttpCollector addCollector(DataStore d, String name)
	{
		return addCollector(d, name, "all");
	}
	
	@Test
	public void testUpdateResultsEmpty()
	{
		tested.updateResults(d);
		assertTrue(yamiMailSender.callHistory.isEmpty());
		assertTrue(d.resultsByMonitoredApp.isEmpty());
	}
	
	@Test
	public void testUpdateResults_AddsOneResultForOneNodeAndOneCollector()
	{
		addNode(d, "node1", true);
		addCollector(d, "collector1");
		tested.updateResults(d);
		assertEquals(1, yamiMailSender.callHistory.size());
		assertFalse(d.resultsByMonitoredApp.isEmpty());
	}
	
	@Test
	public void testUpdateResults_AddsFourResultsForTwoCollectorsAndTwoNodes()
	{
		addCollector(d, "collector1");
		addCollector(d, "collector2");
		addNode(d, "node1", true);
		addNode(d, "node2", true);
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
		addCollector(d, "collector1", "included_hostname");
		addNode(d, "node1", true);
		tested.updateResults(d);
		assertEquals(0, d.resultsByMonitoredApp.size());
		assertEquals(0, yamiMailSender.callHistory.size());
	}
	
	@Test
	public void testUpdateResults_ResultForIncludedNode()
	{
		addNode(d, "node1", true);
		addCollector(d, "collector1", "node1");
		tested.updateResults(d);
		assertEquals(1, d.resultsByMonitoredApp.size());
		assertEquals(1, yamiMailSender.callHistory.size());
	}
	
	@Test
	public void testUpdateResults_ResultForExcludedNodeWithALLIncluded()
	{
		addNode(d, "node1", true);
		HttpCollector hc = addCollector(d, "collector1");
		hc.excludedNodes.add("node1");
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
		HttpCollector hc = addCollector(d, "slave");
		hc.dependsOn.add("master");
		addNode(d, "node1", true);
		
		CollectorHttpResultFetcher fetcher = new ForTestingCollectorHttpResultFetcher();
		yamiMailSender = new ForTestingMailSenderWithDependencyAndPolicyValidation();
		tested = new UpdaterThread(yamiMailSender, fetcher);
		
		tested.updateResults(d);
		assertEquals(0, yamiMailSender.callHistory.size());
	}
	
	@Test
	public void testMonitorDependencyMasterOKShouldMail() throws Exception
	{
		addNode(d, "node1", true);
		HttpCollector master = addCollector(d, "master");
		HttpCollector hc = addCollector(d, "slave");
		hc.dependsOn().add(master);
		tested.updateResults(d);
		assertEquals(2, yamiMailSender.callHistory.size());
	}
	
	@Test
	public void testMonitorDependencyMasterFailedPreventSendingMail() throws Exception
	{
		addNode(d, "node1", true);
		HttpCollector master = addCollector(d, "master");
		HttpCollector hc = addCollector(d, "slave");
		hc.dependsOn().add(master);
		HttpCollector hc2 = addCollector(d, "slave2");
		hc2.dependsOn().add(master);
		ForTestingSendMailStrategy sendMailStrategy = new ForTestingSendMailStrategy();
		YamiMailSender yamiMailSender1 = new YamiMailSender(sendMailStrategy);
		new UpdaterThread(yamiMailSender1, new ForTestingFailingCollectorHttpResultFetcher()).updateResults(d);
		assertEquals(1, sendMailStrategy.sent());
	}
	
	@Before
	public void set()
	{
		List<HttpCollector> c = new ArrayList<HttpCollector>();
		List<Node> a = new ArrayList<Node>();
		d = new ForTestingDataStore(c, a);
		CollectorHttpResultFetcher fetcher = new ForTestingCollectorHttpResultFetcher();
		yamiMailSender = new ForTestingMailSender();
		tested = new UpdaterThread(yamiMailSender, fetcher);
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
	
	private HttpCollector createHttpCollector(String name, String included)
	{
		HttpCollector c = createHttpCollector();
		c.name = name;
		c.includedNodes.add(included);
		return c;
	}
	
}
