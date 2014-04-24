package codeine.jsons.peer_status;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.db.IStatusDatabaseConnector;
import codeine.db.mysql.connectors.StatusDatabaseConnectorListProvider;

import com.google.common.collect.Maps;

public class PeersProjectsStatusInWebServer implements PeersProjectsStatus {

	private static final Logger log = Logger.getLogger(PeersProjectsStatusInDirectory.class);
	public static final long SLEEP_TIME = TimeUnit.SECONDS.toMillis(5);
	private StatusDatabaseConnectorListProvider statusDatabaseConnectorListProvider;
	private Map<String, PeerStatusJsonV2> peer_to_projects = Maps.newHashMap();

	@Inject
	public PeersProjectsStatusInWebServer(StatusDatabaseConnectorListProvider statusDatabaseConnectorListProvider) {
		super();
		this.statusDatabaseConnectorListProvider = statusDatabaseConnectorListProvider;
	}

	@Override
	public void run() {
		log.debug("getting data from directory");
		Map<String, PeerStatusJsonV2> res = Maps.newHashMap();
		for (IStatusDatabaseConnector c : statusDatabaseConnectorListProvider.get()) {
			Map<String, PeerStatusJsonV2> peersStatus = c.getPeersStatus();
			for (Entry<String, PeerStatusJsonV2> e : peersStatus.entrySet()) {
				if (!res.containsKey(e.getKey())) {
					res.put(e.getKey(), e.getValue());
				} else { // more than one
					log.info("peer appears in more than one Database " + e.getKey() + " new db: " + c.server());
					if (isNewer(e.getValue(), res.get(e.getKey()))) {

					}
				}
			}
		}
		peer_to_projects = res;
	}

	private boolean isNewer(PeerStatusJsonV2 newOne, PeerStatusJsonV2 oldOne) {
		if (newOne.update_time_from_peer() > 0 || oldOne.update_time_from_peer() > 0) {
			return newOne.update_time_from_peer() > oldOne.update_time_from_peer();
		}
		return newOne.update_time() > oldOne.update_time();
	}

	@Override
	public Map<String, PeerStatusJsonV2> peer_to_projects() {
		return peer_to_projects;
	}

}
