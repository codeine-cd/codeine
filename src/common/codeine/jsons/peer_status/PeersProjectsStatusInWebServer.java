package codeine.jsons.peer_status;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.db.IStatusDatabaseConnector;
import codeine.db.mysql.connectors.StatusDatabaseConnectorListProvider;
import codeine.utils.ThreadUtils;

import com.google.common.base.Stopwatch;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class PeersProjectsStatusInWebServer implements PeersProjectsStatus {

	private static final Logger log = Logger.getLogger(PeersProjectsStatusInWebServer.class);
	public static final long SLEEP_TIME = TimeUnit.SECONDS.toMillis(5);
	private StatusDatabaseConnectorListProvider statusDatabaseConnectorListProvider;
	private Map<String, PeerStatusJsonV2> peer_to_projects = Maps.newHashMap();
	private Cache<String, Map<String, PeerStatusJsonV2>> cache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build();
	
	@Inject
	public PeersProjectsStatusInWebServer(StatusDatabaseConnectorListProvider statusDatabaseConnectorListProvider) {
		super();
		this.statusDatabaseConnectorListProvider = statusDatabaseConnectorListProvider;
	}

	@Override
	public void run() {
		log.debug("getting data from directory");
		List<Map<String, PeerStatusJsonV2>> updateMaps = getUpdateMaps();
		peer_to_projects = mergeUpdateMaps(updateMaps);
	}

	private Map<String, PeerStatusJsonV2> mergeUpdateMaps(List<Map<String, PeerStatusJsonV2>> updateMaps) {
		log.info("mergin maps");
		int duplicatePeers = 0;
		Map<String, PeerStatusJsonV2> res = Maps.newHashMap();
		for (Map<String, PeerStatusJsonV2> peersStatus : updateMaps) {
			for (Entry<String, PeerStatusJsonV2> e : peersStatus.entrySet()) {
				if (!res.containsKey(e.getKey())) {
					res.put(e.getKey(), e.getValue());
				} else { // more than one
					log.debug("peer appears in more than one database " + e.getKey());
					duplicatePeers++;
					if (isNewer(e.getValue(), res.get(e.getKey()))) {
						res.put(e.getKey(), e.getValue());
					}
				}
			}
		}
		log.info("total duplicate peers " + duplicatePeers);
		return res;
	}

	private List<Map<String, PeerStatusJsonV2>> getUpdateMaps() {
		List<IStatusDatabaseConnector> providers = statusDatabaseConnectorListProvider.get();
		log.info("will get update concurrent with pool size " + providers.size());
		Map<String, FutureTask<Map<String, PeerStatusJsonV2>>> futures = Maps.newHashMap();
		ExecutorService executor = ThreadUtils.newFixedThreadPool(providers.size(), "PeersProjectsStatus");
		for (final IStatusDatabaseConnector c : providers) {
			FutureTask<Map<String, PeerStatusJsonV2>> future = createFuture(c);
			futures.put(c.server(), future);
			executor.execute(future);
		}
		executor.shutdown();
		waitForExecutors(executor);
		putDataInCache(futures);
		return Lists.newArrayList(cache.asMap().values());
	}

	private FutureTask<Map<String, PeerStatusJsonV2>> createFuture(final IStatusDatabaseConnector c) {
		FutureTask<Map<String, PeerStatusJsonV2>> future = new FutureTask<Map<String, PeerStatusJsonV2>>(new Callable<Map<String,PeerStatusJsonV2>>() {
			@Override
			public Map<String, PeerStatusJsonV2> call() throws Exception {
				Stopwatch s = Stopwatch.createStarted();
				Map<String, PeerStatusJsonV2> peersStatus = c.getPeersStatus();
				log.info("getting status from db " + c + " took " + s);
				return peersStatus;
			}
		});
		return future;
	}

	private void waitForExecutors(ExecutorService executor) {
		while (!executor.isTerminated()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}

	private void putDataInCache(Map<String, FutureTask<Map<String, PeerStatusJsonV2>>> futures) {
		for (Entry<String, FutureTask<Map<String, PeerStatusJsonV2>>> entry : futures.entrySet()) {
			try {
				Map<String, PeerStatusJsonV2> map = entry.getValue().get();
				if (map.isEmpty()) {
					log.info("database is empty " + entry.getKey());
				}
				else {
					cache.put(entry.getKey(), map);
				}
			} catch (Exception e) {
				log.warn("failed to get peers from database " + entry.getKey(), e);
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

}
