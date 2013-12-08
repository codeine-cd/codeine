package codeine.jsons.peer_status;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.db.IStatusDatabaseConnector;
import codeine.executer.Task;

import com.google.common.collect.Maps;

/**
 * TODO dont forget it is used also on directory (split before merges)
 */
public class PeersProjectsStatus implements Task {
	
	private static final Logger log = Logger.getLogger(PeersProjectsStatus.class);
	public static final long SLEEP_TIME = TimeUnit.SECONDS.toMillis(5);
	@Inject private IStatusDatabaseConnector statusDatabaseConnector;
	private Map<String, PeerStatusJsonV2> peer_to_projects = Maps.newHashMap();

	@Override
	public void run() {
		log.debug("getting data from directory");
//		Map<String, PeerStatusJsonV2> newMap = Maps.newHashMap();
//		Map<String, PeerStatusJsonV2> peersProjectStatus = statusDatabaseConnector.getPeersStatus();
//		for (PeerStatusJsonV2 peerStatus : peersProjectStatus.values()) {
////			if (peerStatus.isExpired()){
////				log.info("peer expired, will ignore " + peerStatus.peer_key());
////				continue;
////			}
//			newMap.put(peerStatus.key(), peerStatus);
//		}
//		peer_to_projects = newMap;
		peer_to_projects = statusDatabaseConnector.getPeersStatus();
	}

	public Map<String, PeerStatusJsonV2> peer_to_projects() {
		return peer_to_projects;
	}

}
