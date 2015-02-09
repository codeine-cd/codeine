package codeine.mail;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.api.NodeInfo;
import codeine.db.IAlertsDatabaseConnector;
import codeine.jsons.mails.CollectorNotificationJson;
import codeine.jsons.nodes.NodeHelper;
import codeine.jsons.peer_status.PeerStatus;
import codeine.jsons.project.ProjectJson;
import codeine.utils.network.InetUtils;

public class NotificationDeliverToDatabase {

	private static final Logger log = Logger.getLogger(NotificationDeliverToDatabase.class);
	@Inject private PeerStatus peerStatus;
	@Inject private IAlertsDatabaseConnector alertsConnector;
	
	public void sendCollectorResult(String collectorName, NodeInfo node,
			ProjectJson project, String output, int exit_status, String duration, boolean is_for_collector, int notifications_in_24h) {
		String version = new NodeHelper().getVersionOrNull(peerStatus.createJson(), project, node);
		CollectorNotificationJson collectorNotificationJson = new CollectorNotificationJson(collectorName,
				project.name(), output, node.name(), node.alias(), version, InetUtils.getLocalHost().getHostName(), exit_status, duration, is_for_collector, notifications_in_24h);
		log.info("sending notification " + collectorNotificationJson.toStringNoOutput());
		alertsConnector.put(collectorNotificationJson);
	}

}
