package codeine;


import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.log4j.Logger;

import codeine.api.NodeGetter;
import codeine.api.NodeInfo;
import codeine.api.NodeWithMonitorsInfo;
import codeine.configuration.Links;
import codeine.executer.ThreadPoolUtils;
import codeine.jsons.nodes.NodeDiscoveryStrategy;
import codeine.jsons.peer_status.PeerStatusJsonV2;
import codeine.jsons.peer_status.PeerType;
import codeine.jsons.peer_status.PeersProjectsStatus;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.utils.ExceptionUtils;
import codeine.utils.StringUtils;
import codeine.utils.network.HttpUtils;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;


public class ProjectConfigurationInPeerUpdater{

	private static final Logger log = Logger.getLogger(ProjectConfigurationInPeerUpdater.class);
	@Inject	private PeersProjectsStatus peersProjectsStatus;
	@Inject	private NodeGetter nodeGetter;
	@Inject	private Links links;
	private ThreadPoolExecutor threadPool = ThreadPoolUtils.newThreadPool(10);
	
	
	private void sendUpdateToPeers(Collection<PeerStatusJsonV2> allPeers) {
		Stopwatch s = Stopwatch.createStarted();
		List<PeerStatusJsonV2> failedPeers = Lists.newArrayList();
		log.info("sending refresh request to " + allPeers.size() + " peers");
		for (PeerStatusJsonV2 e : allPeers) {
			try {
				if (e.peer_type() == PeerType.Reporter) {
					log.debug("reporter peer, will not push configuration " + e);
					continue;
				}
				String result = HttpUtils.doGET(links.getPeerLink(e.address_port() + Constants.RELOAD_CONFIGURATION_CONTEXT),null, HttpUtils.SHORT_READ_TIMEOUT_MILLI);
				log.debug("updated " + e.address_port() + " result " + result);
			} catch (Exception e1) {
				failedPeers.add(e);
				log.warn("fail to push conf " + e.address_port() + "  " + ExceptionUtils.getRootCauseMessage(e1));
			}
		}
		log.info("finished to send refresh request to " + allPeers.size() + " peers, failed to send to " + failedPeers.size() + " ; took " + s);
	}


	public void updateAllPeers() {
		executeOnThreadPool(peersProjectsStatus.peer_to_projects().values());
	}


	private void executeOnThreadPool(final Collection<PeerStatusJsonV2> allPeers) {
		log.info("adding task to thread pool " + threadPool);
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				sendUpdateToPeers(allPeers);
			}
		});
	}


	public void updatePeers(ProjectJson updatedProject, ProjectJson previousProject) {
		Collection<PeerStatusJsonV2> peers = Sets.newHashSet();
		List<NodeWithMonitorsInfo> nodes = nodeGetter.getNodes(updatedProject.name());
		for (NodeWithMonitorsInfo node : nodes) {
			peers.add(node.peer());
		}
		peers.addAll(getPeersFromProject(updatedProject));
		peers.addAll(getPeersFromProject(previousProject));
		executeOnThreadPool(peers);
	}


	private Collection<? extends PeerStatusJsonV2> getPeersFromProject(ProjectJson project) {
		Collection<PeerStatusJsonV2> peers = Sets.newHashSet();
		if (null == project || project.node_discovery_startegy() != NodeDiscoveryStrategy.Configuration) {
			return peers;
		}
		Set<String> nodes = Sets.newHashSet();
		for (NodeInfo node : project.nodes_info()) {
			String name = node.name();
			if (name.contains(":")) {
				name = name.substring(0, name.indexOf(":"));
			}
			nodes.add(name.toLowerCase());
		}
		for (PeerStatusJsonV2 peer : peersProjectsStatus.peer_to_projects().values()) {
			if (null != peer.host()) {
				if (nodes.contains(StringUtils.toLowerCase(peer.host()))) {
					peers.add(peer);
				}
			}
		}
		return peers;
	}

	
}
