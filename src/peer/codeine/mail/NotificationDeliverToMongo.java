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

public class NotificationDeliverToMongo {

	private static final Logger log = Logger.getLogger(NotificationDeliverToMongo.class);
	@Inject private PeerStatus peerStatus;
	@Inject private IAlertsDatabaseConnector mongoConnector;
	
	public void sendCollectorResult(String collectorName, NodeInfo node,
			ProjectJson project, String output) {
		String version = new NodeHelper().getVersionOrNull(peerStatus.createJson(), project, node);
		CollectorNotificationJson collectorNotificationJson = new CollectorNotificationJson(collectorName,
				project.name(), output, node, version, InetUtils.getLocalHost().getHostName());
		log.debug("sending " + collectorNotificationJson);
		mongoConnector.put(collectorNotificationJson);
	}

}
