package yami;

import java.util.List;

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
		if (shouldMailByPolicies(d.mailingPolicy(), state))
		{
			sendMailStrategy.sendMail(d.mailingList(), c, n, state.getLast());
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
}