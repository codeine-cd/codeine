package yami;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import yami.configuration.HttpCollector;
import yami.configuration.MailPolicy;
import yami.configuration.Node;
import yami.configuration.Peer;
import yami.mail.CollectorOnAppState;
import yami.model.IDataStore;
import yami.model.Result;

public class YamiMailSenderTest
{
	
	@Test
	public void testSendMailIfNeededSimple()
	{
		CollectorOnAppState state = new CollectorOnAppState();
		Result r = new Result(0, null);
		state.addResult(r );
		Node n = new Node();
		n.name = "stam";
		Peer peer = new Peer();
		peer.name = "peername";
		n.node = peer;
		HttpCollector c = new HttpCollector();
		c.name = "cname";
		ForTestingIDataStore d = new ForTestingIDataStore(MailPolicy.EachRun);
		ForTestingSendMailStrategy a = new ForTestingSendMailStrategy();
		new YamiMailSender(a ).sendMailIfNeeded(d, c, n, state);
		assertTrue(a.sent);
	}
	@Test
	public void testDontSendMail()
	{
		CollectorOnAppState state = new CollectorOnAppState();
		Result r = new Result(1, null);
		state.addResult(r );
		Node n = new Node();
		HttpCollector c = new HttpCollector();
		ForTestingIDataStore d = new ForTestingIDataStore(MailPolicy.BackToNormal);
		ForTestingSendMailStrategy a = new ForTestingSendMailStrategy();
		new YamiMailSender(a ).sendMailIfNeeded(d, c, n, state);
		assertFalse(a.sent);
	}
	@Test
	public void testDontSendMailIfStateIsNull()
	{
		CollectorOnAppState state = null;
		Node n = new Node();
		HttpCollector c = new HttpCollector();
		ForTestingIDataStore d = new ForTestingIDataStore(MailPolicy.BackToNormal);
		ForTestingSendMailStrategy a = new ForTestingSendMailStrategy();
		new YamiMailSender(a ).sendMailIfNeeded(d, c, n, state);
		assertFalse(a.sent);
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
			return null;
		}
		
	}
	public static class ForTestingSendMailStrategy extends SendMailStrategy
	{
		private boolean sent;

		@Override
		public void sendMail(List<String> mailingList, HttpCollector c, Node n, Result results)
		{
			sent = true;
		}
	}
}
