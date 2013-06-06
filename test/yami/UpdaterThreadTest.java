package yami;

import static com.google.common.collect.Lists.*;
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
import yami.mail.CollectorOnNodeState;
import yami.model.DataStore;
import yami.model.Result;

import com.google.common.collect.Lists;

public class UpdaterThreadTest
{
	private YamiMailSender yamiMailSender;
	private DataStore d;
	private UpdaterThread tested;
	private ForTestingSendMailStrategy sendMailStrategy;
	
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
	
	private final class ForTestingDataStore extends DataStore
	{
		private final List<HttpCollector> collectors;
		private final List<Node> apps;
		private List<MailPolicy> l = Lists.newArrayList(MailPolicy.EachRun);
		
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
			return l;
		}
		
		public void mailingPolicy(ArrayList<MailPolicy> l)
		{
			this.l = l;
		}
		
		@Override
		public List<HttpCollector> implicitCollectors()
		{
			return newArrayList();
		}
		
		@Override
		public List<Peer> peers()
		{
			return newArrayList();
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
		assertEquals(0, size());
		assertTrue(d.resultsByNode.isEmpty());
	}
	
	@Test
	public void testUpdateResults_AddsOneResultForOneNodeAndOneCollector()
	{
		addNode(d, "node1", true);
		addCollector(d, "collector1");
		tested.updateResults(d);
		// when(d.mailingPolicy()).thenReturn(Lists.newArrayList(MailPolicy.EachRun));
		assertEquals(1, size());
		assertFalse(d.resultsByNode.isEmpty());
	}
	
	@Test
	public void testUpdateResults_AddsFourResultsForTwoCollectorsAndTwoNodes()
	{
		addCollector(d, "collector1");
		addCollector(d, "collector2");
		addNode(d, "node1", true);
		addNode(d, "node2", true);
		tested.updateResults(d);
		assertEquals(2, d.resultsByNode.size());
		assertEquals(4, size());
		for (Entry<Node, Map<HttpCollector, CollectorOnNodeState>> e : d.resultsByNode.entrySet())
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
		assertEquals(0, d.resultsByNode.size());
		assertEquals(0, size());
	}
	
	@Test
	public void testUpdateResults_ResultForIncludedNode()
	{
		addNode(d, "node1", true);
		addCollector(d, "collector1", "node1");
		tested.updateResults(d);
		assertEquals(1, d.resultsByNode.size());
		assertEquals(1, size());
	}
	
	@Test
	public void testUpdateResults_ResultForExcludedNodeWithALLIncluded()
	{
		addNode(d, "node1", true);
		HttpCollector hc = addCollector(d, "collector1");
		hc.excludedNodes.add("node1");
		tested.updateResults(d);
		assertEquals(0, d.resultsByNode.size());
		assertEquals(0, size());
		addNode(d, "node2", true);
		tested.updateResults(d);
		assertEquals(1, d.resultsByNode.size());
		assertEquals(1, size());
	}
	
	@Test
	public void testMonitorDependencyPreventSendingMail() throws Exception
	{
		HttpCollector hc = addCollector(d, "slave");
		// d = mock(DataStore.class);
		hc.dependsOn().add(addCollector(d, "master"));
		((ForTestingDataStore)d).mailingPolicy(Lists.newArrayList(MailPolicy.NewFailure));
		addNode(d, "node1", true);
		// when(d.mailingPolicy()).thenReturn(Lists.newArrayList(MailPolicy.NewFailure));
		CollectorHttpResultFetcher fetcher = new ForTestingFailingCollectorHttpResultFetcher();
		tested = new UpdaterThread(createYamiMailSender(), fetcher, false);
		tested.updateResults(d);
		assertEquals(1, sendMailStrategy.sent());
	}
	
	private YamiMailSender createYamiMailSender()
	{
		return new YamiMailSender(sendMailStrategy);
	}
	
	@Test
	public void testMonitorDependencyMasterOKShouldMail() throws Exception
	{
		addNode(d, "node1", true);
		HttpCollector master = addCollector(d, "master");
		HttpCollector hc = addCollector(d, "slave");
		hc.dependsOn().add(master);
		tested.updateResults(d);
		assertEquals(2, size());
	}
	
	@Test
	public void testMonitorDependencyMasterAndSlaveBackToNormal() throws Exception
	{
		addNode(d, "node1", true);
		HttpCollector master = addCollector(d, "master");
		HttpCollector hc = addCollector(d, "slave");
		hc.dependsOn().add(master);
		// fail once for both collector (mail once):
		((ForTestingDataStore)d).mailingPolicy(Lists.newArrayList(MailPolicy.NewFailure));
		new UpdaterThread(yamiMailSender, new ForTestingFailingCollectorHttpResultFetcher(), false).updateResults(d);
		assertEquals(1, size());
		// succeed twice for both collector (mail once):
		((ForTestingDataStore)d).mailingPolicy(Lists.newArrayList(MailPolicy.BackToNormal));
		new UpdaterThread(yamiMailSender, new ForTestingCollectorHttpResultFetcher(), false).updateResults(d);
		new UpdaterThread(yamiMailSender, new ForTestingCollectorHttpResultFetcher(), false).updateResults(d);
		assertEquals(2, size());
	}
	
	private int size()
	{
		return sendMailStrategy.sent();
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
		((ForTestingDataStore)d).mailingPolicy(Lists.newArrayList(MailPolicy.EachFailure));
		new UpdaterThread(yamiMailSender, new ForTestingFailingCollectorHttpResultFetcher(), false).updateResults(d);
		assertEquals(1, sendMailStrategy.sent());
	}
	
	@Test
	public void testFirstMailNotSent() throws Exception
	{
		addNode(d, "node1", true);
		addCollector(d, "master");
		tested = new UpdaterThread(yamiMailSender, new ForTestingFailingCollectorHttpResultFetcher(), true);
		tested.updateResults(d);
		assertEquals(0, size());
	}
	
	@Test
	public void testFirstMailNotSentSecondMailSent() throws Exception
	{
		addNode(d, "node1", true);
		addCollector(d, "master");
		tested = new UpdaterThread(yamiMailSender, new ForTestingFailingCollectorHttpResultFetcher(), true);
		UpdaterThread.isFirstIteration = true;
		tested.updateResults(d);
		tested.updateResults(d);
		assertEquals(1, size());
	}
	
	@Before
	public void setUp()
	{
		List<HttpCollector> c = new ArrayList<HttpCollector>();
		List<Node> a = new ArrayList<Node>();
		d = new ForTestingDataStore(c, a);
		CollectorHttpResultFetcher fetcher = new ForTestingCollectorHttpResultFetcher();
		sendMailStrategy = new ForTestingSendMailStrategy();
		yamiMailSender = createYamiMailSender();
		tested = new UpdaterThread(yamiMailSender, fetcher, false);
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
