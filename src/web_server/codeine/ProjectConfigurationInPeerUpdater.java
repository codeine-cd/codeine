package codeine;


import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import codeine.configuration.Links;
import codeine.executer.NotifiableTask;
import codeine.jsons.peer_status.PeerStatusJsonV2;
import codeine.jsons.peer_status.PeerType;
import codeine.jsons.peer_status.PeersProjectsStatus;
import codeine.model.Constants;
import codeine.utils.ExceptionUtils;
import codeine.utils.network.HttpUtils;

import com.google.common.collect.Lists;
import com.google.inject.Inject;


public class ProjectConfigurationInPeerUpdater  implements NotifiableTask{

	private static final Logger log = Logger.getLogger(ProjectConfigurationInPeerUpdater.class);
	public static final long MAX_SLEEP_TIME_MILLI = TimeUnit.HOURS.toMillis(2);
	@Inject	private PeersProjectsStatus peersProjectsStatus;
	@Inject	private Links links;
	private Object sleepObject = new Object();
	volatile boolean restart = false;
	
	public void updateAllPeers() {
		synchronized (sleepObject) {
			sleepObject.notifyAll();
		}
	}
	@Override
	public void run() {
		restart = true;
		int numTry = 1;
		Collection<PeerStatusJsonV2> allPeers = peersProjectsStatus.peer_to_projects().values();
		List<PeerStatusJsonV2> failedPeers = Lists.newArrayList();
		while (restart) {
			restart = false;
			failedPeers = Lists.newArrayList();
			log.info("sending refresh request to " + allPeers.size() + " peers");
			for (PeerStatusJsonV2 e : allPeers) {
				try {
					if (e.peer_type() == PeerType.Reporter) {
						log.debug("reporter peer, will not push configuration " + e);
						continue;
					}
					String result = HttpUtils.doGET(links.getPeerLink(e.host_port() + Constants.RELOAD_CONFIGURATION_CONTEXT),null, HttpUtils.SHORT_READ_TIMEOUT_MILLI);
					log.debug("updated " + e.host_port() + " result " + result);
				} catch (Exception e1) {
					failedPeers.add(e);
					log.warn("fail to push conf " + e.host_port() + "  " + ExceptionUtils.getRootCauseMessage(e1));
				}
				if (restart) {
					break;
				}
			}
			if ((numTry++ <= 3) && (failedPeers.size() > 0)) {
				log.info("Will retry to send update to " + failedPeers.size() + " peers");
				restart = true;
				allPeers = Lists.newArrayList(failedPeers);
				failedPeers.clear();
			}
		}
		log.info("finished to send refresh request to " + allPeers.size() + " peers, failed to send to " + failedPeers.size());
	}

	@Override
	public Object getSleepObject() {
		return sleepObject;
	}

	
}
