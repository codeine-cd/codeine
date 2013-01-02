package yami;

import java.util.List;

import yami.configuration.HttpCollector;
import yami.configuration.MailPolicy;
import yami.configuration.Node;
import yami.mail.CollectorOnNodeState;
import yami.model.IDataStore;

import com.google.common.collect.Lists;

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
		if (!shouldMailByState(c, n, d))
		{
			return;
		}
		
		if (!shouldMailByPolicies(d.mailingPolicy(), state))
		{
			return;
		}
		
		List<String> mailingList = composeMailingList(d, n, c);
		sendMailStrategy.mailCollectorResult(mailingList, c, n, state.getLast());
	}
	
	List<String> composeMailingList(IDataStore d, Node n, HttpCollector c)
	{
		List<String> mailingList = Lists.newLinkedList();
		if (n != null)
		{
			if (n.peer != null)
			{
				mailingList.addAll(n.peer.mailingList());
			}
		}
		if (d != null)
		{
			mailingList.addAll(d.mailingList());
		}
		if (c != null)
		{
			mailingList.addAll(c.mailingList());
		}
		return mailingList;
	}
	
	protected boolean shouldMailByPolicies(List<MailPolicy> policies, CollectorOnNodeState state)
	{
		if (null == state || null == policies)
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
	
	protected boolean shouldMailByState(HttpCollector c, Node n, IDataStore d)
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
