package codeine.mail;

import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.api.NodeInfo;
import codeine.configuration.ConfiguredProjectUtils;
import codeine.configuration.NodeMonitor;
import codeine.jsons.peer_status.PeerStatus;
import codeine.jsons.project.ProjectJson;

public class MailSender
{
	private static final Logger log = Logger.getLogger(MailSender.class);
	@Inject private SendMailDirectly collectorStrategy;
	@Inject private ConfiguredProjectUtils configuredProjectUtils;
	@Inject private PeerStatus projectStatusList;
	
	public void sendMailIfNeeded(boolean result, boolean previousResult, NodeMonitor collector, NodeInfo node, String output, ProjectJson project)
	{
		ShouldSendMailValidator needMailValidator = new ShouldSendMailValidator(result, previousResult, collector, configuredProjectUtils, project, node, projectStatusList);
		if (!needMailValidator.shouldMail())
		{
			return;
		}
		List<String> mailingList = composeMailingList(collector, project);
		log.info("sending mail to " + mailingList);
		collectorStrategy.sendCollectorResult(mailingList, collector.name(), result, node, project, output);
	}
	
	private List<String> composeMailingList(NodeMonitor collector, ProjectJson project)
	{
		throw new UnsupportedOperationException("not enabled for now");
	}
}
