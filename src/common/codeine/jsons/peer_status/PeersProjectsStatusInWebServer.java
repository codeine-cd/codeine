package codeine.jsons.peer_status;

import codeine.db.IStatusDatabaseConnector;
import codeine.db.mysql.connectors.StatusDatabaseConnectorListProvider;
import codeine.executer.PeriodicExecuter;
import codeine.executer.Task;
import codeine.utils.StringUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.apache.log4j.Logger;

public class PeersProjectsStatusInWebServer implements PeersProjectsStatus {

	private static final Logger log = Logger.getLogger(PeersProjectsStatusInWebServer.class);
	public static final long SLEEP_TIME = TimeUnit.SECONDS.toMillis(55);
	private long WORKER_SLEEP_TIME = TimeUnit.SECONDS.toMillis(30);
	private StatusDatabaseConnectorListProvider statusDatabaseConnectorListProvider;
	private Cache<String, PeerStatusJsonV2> cache = CacheBuilder.newBuilder().expireAfterWrite(20, TimeUnit.MINUTES).build();
	private Map<String, PeriodicExecuter> connectorsMap = Maps.newHashMap();
	
	@Inject
	public PeersProjectsStatusInWebServer(StatusDatabaseConnectorListProvider statusDatabaseConnectorListProvider) {
		super();
		this.statusDatabaseConnectorListProvider = statusDatabaseConnectorListProvider;
        if (!StringUtils.isEmpty(System.getProperty("REFRESH_SECONDS"))) {
            try {
                WORKER_SLEEP_TIME = TimeUnit.SECONDS.toMillis(Long.parseLong(System.getProperty("REFRESH_SECONDS"), 10));
            }
            catch(Exception e) {
                log.error("Failed to parse REFRESH_SECONDS property", e);
            }
        }
        log.info("refresh rate is " + WORKER_SLEEP_TIME + " milliseconds");
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
				PeriodicExecuter e = new PeriodicExecuter(WORKER_SLEEP_TIME, w, "PeersStatusWorker-" + connector.server());
				e.runInThread();
				connectorsMap.put(connector.server(), e);
			}
		}
		for (Entry<String, PeriodicExecuter> e : connectorsToRemove.entrySet()) {
			log.info("stopping " + e.getKey());
			e.getValue().stopWhenPossible();
		}
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
				} else { // more than one/already in cache
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
			log.debug("peer new do not have update time " + newOne);
		}
		if (oldOne.update_time_from_peer() == 0) {
			log.debug("peer old do not have update time " + oldOne);
		}
		if (newOne.update_time_from_peer() > 0 || oldOne.update_time_from_peer() > 0) {
			return newOne.update_time_from_peer() > oldOne.update_time_from_peer();
		}
		return newOne.update_time() > oldOne.update_time();
	}

	@Override
	public Map<String, PeerStatusJsonV2> peer_to_projects() {
		return getCacheAsMap();
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
