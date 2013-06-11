package yami;

import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import yami.configuration.ConfigurationManager;
import yami.configuration.HttpCollector;
import yami.configuration.Node;
import yami.mail.CollectorOnNodeState;
import yami.model.IDataStore;

import com.google.common.collect.Lists;

public class YamiMailSender
{
	private static final Logger log = Logger.getLogger(YamiMailSender.class);
	private SendMailStrategy sendMailStrategy;
	private final ConfigurationManager configurationManager;
	
	@Inject
	public YamiMailSender(SendMailStrategy sendMailStrategy, ConfigurationManager configurationManager)
	{
		super();
		this.sendMailStrategy = sendMailStrategy;
		this.configurationManager = configurationManager;
	}
	
	public void sendMailIfNeeded(IDataStore d, HttpCollector c, Node n, CollectorOnNodeState state)
	{
		ShouldSendMailValidator needMailValidator = new ShouldSendMailValidator(c, n, state, configurationManager.getConfiguredProject().mailingPolicy(), d);
		if (!needMailValidator.shouldMail())
		{
			return;
		}
		List<String> mailingList = composeMailingList(d, n, c);
		log.info("sending mail to " + mailingList);
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
			mailingList.addAll(configurationManager.getConfiguredProject().mailingList());
		}
		if (c != null && c.mailingList() != null)
		{
			mailingList.addAll(c.mailingList());
		}
		return mailingList;
	}
}
