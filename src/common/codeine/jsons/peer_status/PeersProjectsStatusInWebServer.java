package codeine.jsons.peer_status;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.db.IStatusDatabaseConnector;
import codeine.db.mysql.connectors.StatusDatabaseConnectorListProvider;
import codeine.executer.PeriodicExecuter;
import codeine.executer.Task;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;

public class PeersProjectsStatusInWebServer implements PeersProjectsStatus {

	private static final Logger log = Logger.getLogger(PeersProjectsStatusInWebServer.class);
	public static final long SLEEP_TIME = TimeUnit.SECONDS.toMillis(55);
	public static final long WORKER_SLEEP_TIME = TimeUnit.SECONDS.toMillis(5);
	private StatusDatabaseConnectorListProvider statusDatabaseConnectorListProvider;
	private Map<String, PeerStatusJsonV2> peer_to_projects = Maps.newHashMap();
	private Cache<String, PeerStatusJsonV2> cache = CacheBuilder.newBuilder().expireAfterWrite(20, TimeUnit.MINUTES).build();
	private Map<String, PeriodicExecuter> connectorsMap = Maps.newHashMap();
	
	@Inject
	public PeersProjectsStatusInWebServer(StatusDatabaseConnectorListProvider statusDatabaseConnectorListProvider) {
		super();
		this.statusDatabaseConnectorListProvider = statusDatabaseConnectorListProvider;
	}
	
	@Override
	public void run() {
		log.debug("getting data from directory");
		HashMap<String, PeriodicExecuter> connectorsToRemove = Maps.newHashMap(connectorsMap);
		for (IStatusDatabaseConnector connector : statusDatabaseConnectorListProvider.get()) {
			connectorsToRemove.remove(connector.server());
			if (!connectorsMap.containsKey(connector.server())) {
				log.info("start fetching data from " + connector.server());
				PeersProjectsStatusInWebServerConnectorWorker w = new PeersProjectsStatusInWebServerConnectorWorker(connector);
				PeriodicExecuter e = new PeriodicExecuter(WORKER_SLEEP_TIME, w);
				connectorsMap.put(connector.server(), e);
			}
		}
		for (Entry<String, PeriodicExecuter> e : connectorsToRemove.entrySet()) {
			log.info("stopping " + e.getKey());
			e.getValue().stopWhenPossible();
		}
		peer_to_projects = getCacheAsMap();
	}
	
	private Map<String, PeerStatusJsonV2> getCacheAsMap() {
		Map<String, PeerStatusJsonV2> $ = Maps.newHashMap();
		synchronized (cache) {
			$.putAll(cache.asMap());
		}
		return $;
	}

	private void mergeUpdateMap(Map<String, PeerStatusJsonV2> peersStatus) {
		synchronized (cache) {
			for (Entry<String, PeerStatusJsonV2> e : peersStatus.entrySet()) {
				if (!cache.asMap().containsKey(e.getKey())) {
					cache.put(e.getKey(), e.getValue());
				} else { // more than one
					log.debug("peer appears in more than one database " + e.getKey());
					if (isNewer(e.getValue(), cache.asMap().get(e.getKey()))) {
						cache.put(e.getKey(), e.getValue());
					}
				}
			}
		}
	}


	private boolean isNewer(PeerStatusJsonV2 newOne, PeerStatusJsonV2 oldOne) {
		if (newOne.update_time_from_peer() == 0) {
			log.warn("peer new do not have update time " + newOne);
		}
		if (oldOne.update_time_from_peer() == 0) {
			log.warn("peer old do not have update time " + oldOne);
		}
		if (newOne.update_time_from_peer() > 0 || oldOne.update_time_from_peer() > 0) {
			return newOne.update_time_from_peer() > oldOne.update_time_from_peer();
		}
		return newOne.update_time() > oldOne.update_time();
	}

	@Override
	public Map<String, PeerStatusJsonV2> peer_to_projects() {
		return peer_to_projects;
	}

	public class PeersProjectsStatusInWebServerConnectorWorker implements Task {

		private IStatusDatabaseConnector connector;

		public PeersProjectsStatusInWebServerConnectorWorker(IStatusDatabaseConnector connector) {
			this.connector = connector;
		}

		@Override
		public void run() {
			Map<String, PeerStatusJsonV2> peersStatus = connector.getPeersStatus();
			log.info("got peer status from connector " + connector.server() + " with size " + peersStatus.size());
			mergeUpdateMap(peersStatus);
		}

		@Override
		public String toString() {
			return "PeersProjectsStatusInWebServerConnectorWorker [connector=" + connector + "]";
		}
		
		
	}
}
