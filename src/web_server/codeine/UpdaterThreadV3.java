package codeine;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.db.IStatusDatabaseConnector;
import codeine.executer.Task;
import codeine.jsons.peer_status.PeerStatusJsonV2;
import codeine.jsons.peer_status.PeersProjectsStatus;
import codeine.utils.ExceptionUtils;

import com.google.common.collect.Maps;

public class UpdaterThreadV3 implements Task
{
	private static final Logger log = Logger.getLogger(UpdaterThreadV3.class);
	public static final long SLEEP_TIME = TimeUnit.SECONDS.toMillis(5);
	@Inject private PeersProjectsStatus peersProjectsStatus;
	@Inject private IStatusDatabaseConnector statusDatabaseConnector;

	@Override
	public void run() {
		Map<String, PeerStatusJsonV2> newMap = Maps.newHashMap();
		Map<String, PeerStatusJsonV2> peer_to_projects = peersProjectsStatus.peer_to_projects();
		Map<String, PeerStatusJsonV2> peersProjectStatus = statusDatabaseConnector.getPeersStatus();
		for (PeerStatusJsonV2 peerStatus : peersProjectStatus.values()) {
			if (peerStatus.isExpired()){
				log.info("peer expired, will ignore " + peerStatus.peer_key());
				continue;
			}
			String address = peerStatus.host_port();
			try {
				PeerStatusJsonV2 fetchStatus = fetchStatus(peerStatus, peersProjectStatus);
				if (null != fetchStatus){
					newMap.put(address, fetchStatus);
				}
				else if (peer_to_projects.containsKey(address)){
					newMap.put(address, peer_to_projects.get(address));
				}
			} catch (Exception e) {
				log.warn("failed to get update from peer " + address + "caused by " + ExceptionUtils.getRootCause(e).getMessage());
				log.debug("details of error", e);
			}
		}
		peersProjectsStatus.replaceMap(newMap);
	}

	private PeerStatusJsonV2 fetchStatus(PeerStatusJsonV2 peerStatus, Map<String, PeerStatusJsonV2> peersProjectStatus) {
		return peersProjectStatus.get(peerStatus.key()); 
	}

}
