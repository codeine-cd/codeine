package codeine.mail;

import java.util.List;

import codeine.api.NodeInfo;
import codeine.configuration.Links;
import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.jsons.info.CodeineRuntimeInfo;
import codeine.jsons.labels.LabelJsonProvider;
import codeine.jsons.nodes.NodeHelper;
import codeine.jsons.peer_status.PeerStatus;
import codeine.jsons.project.ProjectJson;
import codeine.utils.network.InetUtils;

import com.google.inject.Inject;

public class SendMailDirectly
{
	
	@Inject private MailStrategy mailsStrategy;
	@Inject private PeerStatus peerStatus;
	@Inject private Links links;
	@Inject	private CodeineRuntimeInfo peerRuntimeInfo;
	@Inject	private LabelJsonProvider labelJsonProvider;
	@Inject	private GlobalConfigurationJsonStore globalConfigurationJsonStore;

	public void sendCollectorResult(List<String> mailingList, String collectorName, boolean results, NodeInfo node, ProjectJson project, String output)
	{
		String successString = results ? "OK" : "FAIL";
		String version = new NodeHelper().getVersionOrNull(peerStatus.createJson(), project, node);
		String versionString = null == version ? "" : " version " + labelJsonProvider.labelForVersion(version, project.name());
		String subject = "codeine monitor '" + collectorName + "' on " + node.alias() + versionString + " is now " + successString;
		String hostname = InetUtils.nameWithoutPort(node.name());
		String content = "Collector current status: " + links.getPeerMonitorResultLink(hostname + ":" +  peerRuntimeInfo.port(), project.name(), collectorName, node.name()) +	"\n\n";
		content += "Alerts: " + links.getWebServerProjectAlerts(project) + "\n\n";
		content += "Collector Output:\n";
		content += output + "\n";
		mailsStrategy.sendMail(new Mail(mailingList, subject, content, globalConfigurationJsonStore.get().admin_mail()));
	}
		
}
