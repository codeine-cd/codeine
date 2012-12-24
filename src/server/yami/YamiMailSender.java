package yami;

import java.util.List;

import org.apache.commons.collections.ListUtils;

import yami.configuration.HttpCollector;
import yami.configuration.MailPolicy;
import yami.configuration.Node;
import yami.mail.CollectorOnAppState;
import yami.model.IDataStore;

public class YamiMailSender
{
	private SendMailStrategy sendMailStrategy;
	
	public YamiMailSender(SendMailStrategy sendMailStrategy)
	{
		super();
		this.sendMailStrategy = sendMailStrategy;
	}
	
	public void sendMailIfNeeded(IDataStore d, HttpCollector c, Node n, CollectorOnAppState state)
	{
		if (!shouldMail(c, n, d))
		{
			return;
		}
		
		if (shouldMailByPolicies(d.mailingPolicy(), state))
		{
			List<String> fullMailingList = ListUtils.union(d.mailingList(), n.node.mailingList);
			sendMailStrategy.mailCollectorResult(fullMailingList, c, n, state.getLast());
		}
	}
	
	private boolean shouldMailByPolicies(List<MailPolicy> mailPolicy, CollectorOnAppState state)
	{
		for (MailPolicy mailPolicy2 : mailPolicy)
		{
			if (null == state)
			{
				return false;
			}
			if (mailPolicy2.isActive(state.prevState(), state.state()))
			{
				return true;
			}
		}
		return false;
	}
	
	private boolean shouldMail(HttpCollector c, Node n, IDataStore d)
	{
		for (HttpCollector master : c.dependsOn())
		{
			CollectorOnAppState r = d.getResult(n, master);
			if (r == null || !r.state())
			{
				return false;
			}
		}
		return true;
	}
}
