package codeine.jsons.peer_status;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.db.IStatusDatabaseConnector;
import codeine.db.mysql.connectors.StatusDatabaseConnectorListProvider;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class PeersProjectsStatusInWebServer implements PeersProjectsStatus {

	private static final Logger log = Logger.getLogger(PeersProjectsStatusInDirectory.class);
	public static final long SLEEP_TIME = TimeUnit.SECONDS.toMillis(5);
	private List<IStatusDatabaseConnector> statusDatabaseConnectorList = Lists.newArrayList();
	private Map<String, PeerStatusJsonV2> peer_to_projects = Maps.newHashMap();

	@Inject
	public PeersProjectsStatusInWebServer(StatusDatabaseConnectorListProvider statusDatabaseConnectorListProvider) {
		super();
		statusDatabaseConnectorList = statusDatabaseConnectorListProvider.get();
	}

	@Override
	public void run() {
		log.debug("getting data from directory");
		Map<String, PeerStatusJsonV2> res = Maps.newHashMap();
		for (IStatusDatabaseConnector c : statusDatabaseConnectorList) {
			Map<String, PeerStatusJsonV2> peersStatus = c.getPeersStatus();
			for (Entry<String, PeerStatusJsonV2> e : peersStatus.entrySet()) {
				if (!res.containsKey(e.getKey()) || isNewer(e.getValue(), res.get(e.getKey()))) {
					res.put(e.getKey(), e.getValue());
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
