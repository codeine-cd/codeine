package codeine.jsons.peer_status;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.db.IStatusDatabaseConnector;

import com.google.common.collect.Maps;

public class PeersProjectsStatusInDirectory implements PeersProjectsStatus {
	
	private static final Logger log = Logger.getLogger(PeersProjectsStatusInDirectory.class);
	public static final long SLEEP_TIME = TimeUnit.SECONDS.toMillis(5);
	@Inject private IStatusDatabaseConnector statusDatabaseConnector;
	private Map<String, PeerStatusJsonV2> peer_to_projects = Maps.newHashMap();

	@Override
	public void run() {
		log.debug("getting data from directory");
		peer_to_projects = statusDatabaseConnector.getPeersStatus();
	}

	@Override
	public Map<String, PeerStatusJsonV2> peer_to_projects() {
		return peer_to_projects;
	}

}
