package yami;

import java.util.List;

import org.apache.commons.collections.ListUtils;

import yami.configuration.HttpCollector;
import yami.configuration.MailPolicy;
import yami.configuration.Node;
import yami.mail.CollectorOnNodeState;
import yami.model.IDataStore;

public class YamiMailSender
{
	private SendMailStrategy sendMailStrategy;
	
	public YamiMailSender(SendMailStrategy sendMailStrategy)
	{
		super();
		this.sendMailStrategy = sendMailStrategy;
	}
	
	public void sendMailIfNeeded(IDataStore d, HttpCollector c, Node n, CollectorOnNodeState state)
	{
		if (!shouldMail(c, n, d))
		{
			return;
		}
		
		if (!shouldMailByPolicies(d.mailingPolicy(), state))
		{
			return;
		}
		
		List<String> fullMailingList = ListUtils.union(d.mailingList(), n.peer.mailingList);
		sendMailStrategy.mailCollectorResult(fullMailingList, c, n, state.getLast());
	}
	
	protected boolean shouldMailByPolicies(List<MailPolicy> policies, CollectorOnNodeState state)
	{
		if (null == state)
		{
			return false;
		}
		for (MailPolicy mailPolicy2 : policies)
		{
			if (mailPolicy2.isActive(state.prevState(), state.state()))
			{
				return true;
			}
		}
		return false;
	}
	
	protected boolean shouldMail(HttpCollector c, Node n, IDataStore d)
	{
		for (HttpCollector master : c.dependsOn())
		{
			CollectorOnNodeState r = d.getResult(n, master);
			if (r == null || !r.state())
			{
				return false;
			}
		}
		return true;
	}
}
