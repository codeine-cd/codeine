package codeine.mail;

import codeine.api.NodeInfo;
import codeine.configuration.ConfiguredProjectUtils;
import codeine.configuration.NodeMonitor;
import codeine.jsons.peer_status.PeerStatus;
import codeine.jsons.project.ProjectJson;

@SuppressWarnings("unused")
public class ShouldSendMailValidator {
	private NodeMonitor collector;
	private ConfiguredProjectUtils configuredProjectUtils;
	private boolean result;
	private boolean previousResult;
	private ProjectJson project;
	private NodeInfo node;
	private PeerStatus peerStatus;

	public ShouldSendMailValidator(boolean result, boolean previousResult, NodeMonitor collector,	ConfiguredProjectUtils configuredProjectUtils, ProjectJson project, NodeInfo node, PeerStatus peerStatus) {
		this.result = result;
		this.previousResult = previousResult;
		this.collector = collector;
		this.configuredProjectUtils = configuredProjectUtils;
		this.project = project;
		this.node = node;
		this.peerStatus = peerStatus;
	}

//	private boolean shouldMailByPolicies(List<MailPolicy> policies) {
//		for (MailPolicy p : policies) {
//			if (p.isActive(previousResult, result)) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	private boolean shouldMailByDependencies() {
//		throw new UnsupportedOperationException();
////		for (HttpCollector master : configuredProjectUtils.dependsOn(collector, project)) {
////			String resultOfMaster = peerStatus.project_name_to_status().get(project.name()).monitor_to_status().get(node.name()).get(master.name());
////			if ("false".equals(resultOfMaster)) {
////				return false;
////			}
////		}
////		return true;
//	}

	public boolean shouldMail() {
		throw new UnsupportedOperationException();
//		List<MailPolicy> calculatedPolicies = project.mailingPolicy();
//		for (CollectorRule rule : collector.rule) // overwrite mailing policies if there are explicit rules
//		{
//			if (rule.mailingPolicy != null && rule.shouldApplyForNode(node.name)) {
//				calculatedPolicies = rule.mailingPolicy;
//			}
//		}
//		return calculatedPolicies.contains(MailPolicy.EachRun)
//				|| (shouldMailByPolicies(calculatedPolicies) && shouldMailByDependencies());
	}
}
