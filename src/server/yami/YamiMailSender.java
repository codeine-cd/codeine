package yami;

import java.util.List;

import yami.configuration.HttpCollector;
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
		ShouldSendMailValidator needMailValidator = new ShouldSendMailValidator(c, n, state, d.mailingPolicy(), d);
		if (!needMailValidator.shouldMail())
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
		if (c != null && c.mailingList() != null)
		{
			mailingList.addAll(c.mailingList());
		}
		return mailingList;
	}
}
