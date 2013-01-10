package yami;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

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

public class MailSenderTest2
{
	
	private ForTestingDataStore d;
	private ForTestingSendMailStrategy sendMailStrategy;
	private YamiMailSender yamiMailSender;
	
	@Before
	public void setUp() throws Exception
	{
		List<HttpCollector> c = new ArrayList<HttpCollector>();
		List<Node> a = new ArrayList<Node>();
		d = new ForTestingDataStore(c, a);
		sendMailStrategy = new ForTestingSendMailStrategy();
		yamiMailSender = new YamiMailSender(sendMailStrategy);
	}
	
	@Test
	public void testComposeMailingList_emptyMailingList()
	{
		List<String> m = yamiMailSender.composeMailingList(null, null, null);
		assertTrue(m.isEmpty());
	}
	
	@Test
	public void testComposeMailingList_DataStorSpecificMailingList()
	{
		d.mailingList().add("mail1");
		List<String> m = yamiMailSender.composeMailingList(d, null, null);
		assertEquals(1, m.size());
	}
	
	@Test
	public void testComposeMailingList_PeerSpecificMailingList()
	{
		Peer p = new Peer();
		p.mailingList().add("mail1");
		Node n = addNode(d, "node1", true);
		n.peer = p;
		List<String> m = yamiMailSender.composeMailingList(d, n, null);
		assertEquals(1, m.size());
	}
	
	@Test
	public void testComposeMailingList_CollectorSpecificMailingList()
	{
		Node n = addNode(d, "node1", true);
		n.peer = null;
		HttpCollector c = new HttpCollector();
		c.mailingList().add("test");
		List<String> m = yamiMailSender.composeMailingList(d, n, c);
		assertEquals(1, m.size());
	}
	
	@Test
	public void testComposeMailingList_3Sources()
	{
		d.mailingList().add("mail1");
		Peer p = new Peer();
		p.mailingList().add("mail2");
		Node n = addNode(d, "node1", true);
		n.peer = p;
		HttpCollector c = new HttpCollector();
		c.mailingList().add("mail3");
		List<String> m = yamiMailSender.composeMailingList(d, n, c);
		assertEquals(3, m.size());
	}
	
	@Test
	public void testShouldMailByPolicies_EmptyPolicies()
	{
		List<MailPolicy> policies = Lists.newArrayList();
		CollectorOnNodeState state = new CollectorOnNodeState();
		assertFalse(yamiMailSender.shouldMailByPolicies(policies, state));
		assertFalse(yamiMailSender.shouldMailByPolicies(policies, null));
		assertFalse(yamiMailSender.shouldMailByPolicies(null, state));
	}
	
	@Test
	public void testShouldMailByPolicies_SinglePolicyEachRun()
	{
		List<MailPolicy> policies = Lists.newArrayList(MailPolicy.EachRun);
		CollectorOnNodeState state = new CollectorOnNodeState();
		assertTrue(yamiMailSender.shouldMailByPolicies(policies, state));
	}
	
	@Test
	public void testShouldMailByPolicies_SinglePolicyBackToNormal()
	{
		List<MailPolicy> policies = Lists.newArrayList(MailPolicy.BackToNormal);
		CollectorOnNodeState state = new CollectorOnNodeState();
		assertFalse(yamiMailSender.shouldMailByPolicies(policies, state));
	}
	
	@Test
	public void testShouldMailByPolicies_SinglePolicyBackToNormalWith1Failure()
	{
		List<MailPolicy> policies = Lists.newArrayList(MailPolicy.BackToNormal);
		CollectorOnNodeState state = new CollectorOnNodeState();
		Result r = new Result(1, "");
		state.addResult(r);
		assertFalse(yamiMailSender.shouldMailByPolicies(policies, state));
	}
	
	@Test
	public void testShouldMailByPolicies_SinglePolicyBackToNormalWith1FailureAnd1Success()
	{
		List<MailPolicy> policies = Lists.newArrayList(MailPolicy.BackToNormal);
		CollectorOnNodeState state = new CollectorOnNodeState();
		state.addResult(new Result(1, ""));
		state.addResult(new Result(0, ""));
		assertFalse(yamiMailSender.shouldMailByPolicies(policies, state));
	}
	
	@Test
	public void testShouldMailByPolicies_SinglePolicyBackToNormalWith1FailureAnd2Success()
	{
		List<MailPolicy> policies = Lists.newArrayList(MailPolicy.BackToNormal);
		CollectorOnNodeState state = new CollectorOnNodeState();
		state.addResult(new Result(1, ""));
		state.addResult(new Result(0, ""));
		state.addResult(new Result(0, ""));
		assertTrue(yamiMailSender.shouldMailByPolicies(policies, state));
	}
	
	@Test
	public void testShouldMailByPolicies_SinglePolicyNewFailue()
	{
		List<MailPolicy> policies = Lists.newArrayList(MailPolicy.NewFailure);
		CollectorOnNodeState state = new CollectorOnNodeState();
		assertFalse(yamiMailSender.shouldMailByPolicies(policies, state));
	}
	
	@Test
	public void testShouldMailByPolicies_SinglePolicyNewFailueWith1Failed()
	{
		List<MailPolicy> policies = Lists.newArrayList(MailPolicy.NewFailure);
		CollectorOnNodeState state = new CollectorOnNodeState();
		state.addResult(new Result(1, ""));
		assertTrue(yamiMailSender.shouldMailByPolicies(policies, state));
	}
	
	@Test
	public void testShouldMailByPolicies_SinglePolicyNewFailueWith2FailedResult()
	{
		List<MailPolicy> policies = Lists.newArrayList(MailPolicy.NewFailure);
		CollectorOnNodeState state = new CollectorOnNodeState();
		state.addResult(new Result(1, ""));
		state.addResult(new Result(1, ""));
		assertFalse(yamiMailSender.shouldMailByPolicies(policies, state));
	}
	
	@Test
	public void testShouldMailByPolicies_SinglePolicyNewFailueWith2FailedResult1Success()
	{
		List<MailPolicy> policies = Lists.newArrayList(MailPolicy.NewFailure);
		CollectorOnNodeState state = new CollectorOnNodeState();
		state.addResult(new Result(1, ""));
		state.addResult(new Result(1, ""));
		assertFalse(yamiMailSender.shouldMailByPolicies(policies, state));
	}
	
	@Test
	public void testShouldMailByPolicies_SinglePolicyNewFailueWith1FailedResult1Success()
	{
		List<MailPolicy> policies = Lists.newArrayList(MailPolicy.NewFailure);
		CollectorOnNodeState state = new CollectorOnNodeState();
		state.addResult(new Result(1, ""));
		state.addResult(new Result(0, ""));
		assertFalse(yamiMailSender.shouldMailByPolicies(policies, state));
	}
	
	@Test
	public void testShouldMailByPolicies_SinglePolicyEachFailue()
	{
		List<MailPolicy> policies = Lists.newArrayList(MailPolicy.EachFailure);
		CollectorOnNodeState state = new CollectorOnNodeState();
		assertFalse(yamiMailSender.shouldMailByPolicies(policies, state));
	}
	
	@Test
	public void testShouldMailByPolicies_SinglePolicyEachFailueWithFailures()
	{
		List<MailPolicy> policies = Lists.newArrayList(MailPolicy.EachFailure);
		CollectorOnNodeState state = new CollectorOnNodeState();
		state.addResult(new Result(1, ""));
		assertTrue(yamiMailSender.shouldMailByPolicies(policies, state));
		state.addResult(new Result(1, ""));
		assertTrue(yamiMailSender.shouldMailByPolicies(policies, state));
	}
	
	private final class ForTestingDataStore extends DataStore
	{
		private final List<HttpCollector> collectors;
		private final List<Node> apps;
		private List<String> m = Lists.newArrayList();
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
			return m;
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
	
	private HttpCollector addCollector(DataStore d, String name, String included)
	{
		return addCollector(d, name, included, true);
	}
	
	private HttpCollector addCollector(DataStore d, String name)
	{
		return addCollector(d, name, "all");
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
}
