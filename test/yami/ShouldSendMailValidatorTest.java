package yami;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import yami.configuration.CollectorRule;
import yami.configuration.HttpCollector;
import yami.configuration.MailPolicy;
import yami.configuration.Node;
import yami.mail.CollectorOnNodeState;
import yami.model.DataStore;
import yami.model.IDataStore;
import yami.model.Result;

import com.google.common.collect.Lists;

public class ShouldSendMailValidatorTest
{
	
	private Node node;
	private HttpCollector collector;
	private IDataStore d;
	private CollectorRule rule;
	private List<MailPolicy> policies;
	private CollectorOnNodeState state;
	
	@Before
	public void setUp() throws Exception
	{
		policies = Lists.newArrayList(MailPolicy.EachRun);
		node = new Node("test_node");
		collector = createHttpCollector();
		state = new CollectorOnNodeState();
		d = new ForTestingDataStore(Lists.newArrayList(collector), Lists.newArrayList(node));
		rule = new CollectorRule();
		rule.node = "all";
	}
	
	@Test
	public void testShouldSendMailValidator_CollectorRule_NoRules()
	{
		ShouldSendMailValidator validator = new ShouldSendMailValidator(collector, node, state, policies, d);
		assertTrue(validator.shouldMail());
	}
	
	@Test
	public void testShouldSendMailValidator_CollectorRule_SkipRule()
	{
		rule.mailingPolicy = Lists.newArrayList(); // empty list means no policy
		collector.rules.add(rule);
		ShouldSendMailValidator validator = new ShouldSendMailValidator(collector, node, state, policies, d);
		assertFalse(validator.shouldMail());
	}
	
	@Test
	public void testShouldMailByPolicies_SinglePolicyBackToNormalWith1Failure_CollectorRuleOverridetoEach()
	{
		List<MailPolicy> policies = Lists.newArrayList(MailPolicy.BackToNormal);
		Result r = new Result(1, "");
		state.addResult(r);
		
		rule.mailingPolicy = Lists.newArrayList(MailPolicy.EachRun);
		collector.rules.add(rule);
		
		ShouldSendMailValidator validator = new ShouldSendMailValidator(collector, node, state, policies, d);
		assertTrue(validator.shouldMail());
	}
	
	@Test
	public void testShouldMailByPolicies_EmptyPolicies()
	{
		List<MailPolicy> policies = Lists.newArrayList();
		ShouldSendMailValidator validator = new ShouldSendMailValidator(collector, node, state, policies, d);
		assertFalse(validator.shouldMail());
	}
	
	@Test
	public void testShouldMailByPolicies_SinglePolicyBackToNormal()
	{
		List<MailPolicy> policies = Lists.newArrayList(MailPolicy.BackToNormal);
		ShouldSendMailValidator validator = new ShouldSendMailValidator(collector, node, state, policies, d);
		assertFalse(validator.shouldMail());
	}
	
	@Test
	public void testShouldMailByPolicies_SinglePolicyBackToNormalWith1Failure()
	{
		List<MailPolicy> policies = Lists.newArrayList(MailPolicy.BackToNormal);
		Result r = new Result(1, "");
		state.addResult(r);
		ShouldSendMailValidator validator = new ShouldSendMailValidator(collector, node, state, policies, d);
		assertFalse(validator.shouldMail());
	}
	
	@Test
	public void testShouldMailByPolicies_SinglePolicyBackToNormalWith1FailureAnd1Success()
	{
		List<MailPolicy> policies = Lists.newArrayList(MailPolicy.BackToNormal);
		state.addResult(new Result(1, ""));
		state.addResult(new Result(0, ""));
		ShouldSendMailValidator validator = new ShouldSendMailValidator(collector, node, state, policies, d);
		assertFalse(validator.shouldMail());
	}
	
	@Test
	public void testShouldMailByPolicies_SinglePolicyBackToNormalWith1FailureAnd2Success()
	{
		List<MailPolicy> policies = Lists.newArrayList(MailPolicy.BackToNormal);
		CollectorOnNodeState state = new CollectorOnNodeState();
		state.addResult(new Result(1, ""));
		state.addResult(new Result(0, ""));
		state.addResult(new Result(0, ""));
		ShouldSendMailValidator validator = new ShouldSendMailValidator(collector, node, state, policies, d);
		assertTrue(validator.shouldMail());
	}
	
	@Test
	public void testShouldMailByPolicies_SinglePolicyNewFailue()
	{
		List<MailPolicy> policies = Lists.newArrayList(MailPolicy.NewFailure);
		CollectorOnNodeState state = new CollectorOnNodeState();
		ShouldSendMailValidator validator = new ShouldSendMailValidator(collector, node, state, policies, d);
		assertFalse(validator.shouldMail());
	}
	
	@Test
	public void testShouldMailByPolicies_SinglePolicyNewFailueWith1Failed()
	{
		List<MailPolicy> policies = Lists.newArrayList(MailPolicy.NewFailure);
		CollectorOnNodeState state = new CollectorOnNodeState();
		state.addResult(new Result(1, ""));
		ShouldSendMailValidator validator = new ShouldSendMailValidator(collector, node, state, policies, d);
		
		assertTrue(validator.shouldMail());
	}
	
	@Test
	public void testShouldMailByPolicies_SinglePolicyNewFailueWith2FailedResult()
	{
		List<MailPolicy> policies = Lists.newArrayList(MailPolicy.NewFailure);
		CollectorOnNodeState state = new CollectorOnNodeState();
		state.addResult(new Result(1, ""));
		state.addResult(new Result(1, ""));
		ShouldSendMailValidator validator = new ShouldSendMailValidator(collector, node, state, policies, d);
		assertFalse(validator.shouldMail());
	}
	
	@Test
	public void testShouldMailByPolicies_SinglePolicyNewFailueWith2FailedResult1Success()
	{
		List<MailPolicy> policies = Lists.newArrayList(MailPolicy.NewFailure);
		CollectorOnNodeState state = new CollectorOnNodeState();
		state.addResult(new Result(1, ""));
		state.addResult(new Result(1, ""));
		ShouldSendMailValidator validator = new ShouldSendMailValidator(collector, node, state, policies, d);
		assertFalse(validator.shouldMail());
	}
	
	@Test
	public void testShouldMailByPolicies_SinglePolicyNewFailueWith1FailedResult1Success()
	{
		List<MailPolicy> policies = Lists.newArrayList(MailPolicy.NewFailure);
		CollectorOnNodeState state = new CollectorOnNodeState();
		state.addResult(new Result(1, ""));
		state.addResult(new Result(0, ""));
		ShouldSendMailValidator validator = new ShouldSendMailValidator(collector, node, state, policies, d);
		assertFalse(validator.shouldMail());
	}
	
	@Test
	public void testShouldMailByPolicies_SinglePolicyEachFailue()
	{
		List<MailPolicy> policies = Lists.newArrayList(MailPolicy.EachFailure);
		CollectorOnNodeState state = new CollectorOnNodeState();
		ShouldSendMailValidator validator = new ShouldSendMailValidator(collector, node, state, policies, d);
		assertFalse(validator.shouldMail());
	}
	
	@Test
	public void testShouldMailByPolicies_SinglePolicyEachFailueWithFailures()
	{
		List<MailPolicy> policies = Lists.newArrayList(MailPolicy.EachFailure);
		CollectorOnNodeState state = new CollectorOnNodeState();
		state.addResult(new Result(1, ""));
		ShouldSendMailValidator validator = new ShouldSendMailValidator(collector, node, state, policies, d);
		assertTrue(validator.shouldMail());
		state.addResult(new Result(1, ""));
		validator = new ShouldSendMailValidator(collector, node, state, policies, d);
		assertTrue(validator.shouldMail());
	}
	
	private HttpCollector createHttpCollector()
	{
		return new HttpCollector()
		{
		};
	}
	
	private final class ForTestingDataStore extends DataStore
	{
		private final List<HttpCollector> collectors;
		private final List<Node> nodes;
		private List<String> m = Lists.newArrayList();
		private List<MailPolicy> l = Lists.newArrayList(MailPolicy.EachRun);
		
		public ForTestingDataStore(List<HttpCollector> collectors, List<Node> nodes)
		{
			super();
			this.collectors = collectors;
			this.nodes = nodes;
		}
	}
	
}
