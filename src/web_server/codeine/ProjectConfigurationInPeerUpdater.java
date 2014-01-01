package codeine;


import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import codeine.configuration.Links;
import codeine.executer.NotifiableTask;
import codeine.jsons.peer_status.PeerStatusJsonV2;
import codeine.jsons.peer_status.PeersProjectsStatus;
import codeine.model.Constants;
import codeine.utils.ExceptionUtils;
import codeine.utils.network.HttpUtils;

import com.google.common.collect.Lists;
import com.google.inject.Inject;


public class ProjectConfigurationInPeerUpdater  implements NotifiableTask{

	private static final Logger log = Logger.getLogger(ProjectConfigurationInPeerUpdater.class);
	@Inject	private PeersProjectsStatus peersProjectsStatus;
	@Inject	private Links links;
	private Object sleepObject = new Object();
	volatile boolean restart = false;
	
	public void updateAllPeers() {
		restart = true;
		synchronized (sleepObject) {
			sleepObject.notifyAll();
		}
	}
	@Override
	public void run() {
		int numTry = 1;
		Collection<PeerStatusJsonV2> allPeers = peersProjectsStatus.peer_to_projects().values();
		while (restart) {
			restart = false;
			List<PeerStatusJsonV2> failedPeers = Lists.newArrayList();
			for (PeerStatusJsonV2 e : allPeers) {
				try {
					String result = HttpUtils.doGET(links.getPeerLink(e.host_port() + Constants.RELOAD_CONFIGURATION_CONTEXT),null);
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
				log.info("Will terty to send update to " + failedPeers.size() + " peers");
				restart = true;
				allPeers = Lists.newArrayList(failedPeers);
				failedPeers.clear();
			}
		}
	}

	@Override
	public Object getSleepObject() {
		return sleepObject;
	}

	
}
