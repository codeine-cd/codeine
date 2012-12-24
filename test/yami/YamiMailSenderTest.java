package yami;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import yami.configuration.HttpCollector;
import yami.configuration.MailPolicy;
import yami.configuration.Node;
import yami.configuration.Peer;
import yami.mail.CollectorOnNodeState;
import yami.model.IDataStore;
import yami.model.Result;

public class YamiMailSenderTest
{
	
	@Test
	public void testSendMailIfNeededSimple()
	{
		CollectorOnNodeState state = new CollectorOnNodeState();
		Result r = new Result(0, null);
		state.addResult(r );
		Node n = new Node();
		n.name = "stam";
		Peer peer = new Peer();
		peer.name = "peername";
		n.peer = peer;
		HttpCollector c = createHttpCollector();
		c.name = "cname";
		ForTestingIDataStore d = new ForTestingIDataStore(MailPolicy.EachRun);
		ForTestingSendMailStrategy a = new ForTestingSendMailStrategy();
		new YamiMailSender(a ).sendMailIfNeeded(d, c, n, state);
		assertTrue(a.isSent());
	}
	@Test
	public void testDontSendMail()
	{
		CollectorOnNodeState state = new CollectorOnNodeState();
		Result r = new Result(1, null);
		state.addResult(r );
		Node n = new Node();
		HttpCollector c = createHttpCollector();
		ForTestingIDataStore d = new ForTestingIDataStore(MailPolicy.BackToNormal);
		ForTestingSendMailStrategy a = new ForTestingSendMailStrategy();
		new YamiMailSender(a ).sendMailIfNeeded(d, c, n, state);
		assertFalse(a.isSent());
	}
	@Test
	public void testDontSendMailIfStateIsNull()
	{
		CollectorOnNodeState state = null;
		Node n = new Node();
		HttpCollector c = createHttpCollector();
		ForTestingIDataStore d = new ForTestingIDataStore(MailPolicy.BackToNormal);
		ForTestingSendMailStrategy a = new ForTestingSendMailStrategy();
		new YamiMailSender(a ).sendMailIfNeeded(d, c, n, state);
		assertFalse(a.isSent());
	}
	public static class ForTestingIDataStore implements IDataStore
	{

		private MailPolicy policy;

		public ForTestingIDataStore(MailPolicy mailPolicy)
		{
			policy = mailPolicy;
		}

		@Override
		public List<MailPolicy> mailingPolicy()
		{
			List<MailPolicy> lst = new ArrayList<MailPolicy>();
			lst.add(policy);
			return lst;
		}

		@Override
		public List<String> mailingList()
		{
			return new ArrayList<String>();
		}

		@Override
		public CollectorOnNodeState getResult(Node n, HttpCollector master)
		{
			return null;
		}
		
	}
	public static class ForTestingSendMailStrategy extends SendMailStrategy
	{
		private int sent;

		@Override
		public void mailCollectorResult(List<String> mailingList, HttpCollector c, Node n, Result results)
		{
			sent++;
		}

		public boolean isSent()
		{
			return sent > 0;
		}

		public int sent()
		{
			return sent;
		}
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
