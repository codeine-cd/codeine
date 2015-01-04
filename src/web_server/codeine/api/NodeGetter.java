package codeine.api;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import codeine.configuration.IConfigurationManager;
import codeine.jsons.collectors.CollectorExecutionInfo;
import codeine.jsons.labels.LabelJsonProvider;
import codeine.jsons.nodes.NodeDiscoveryStrategy;
import codeine.jsons.peer_status.PeerStatusJsonV2;
import codeine.jsons.peer_status.PeersProjectsStatus;
import codeine.jsons.peer_status.ProjectStatus;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.version.ViewNodesFilter;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

public class NodeGetter {
	@Inject	private PeersProjectsStatus peersProjectsStatus;
	@Inject	private IConfigurationManager configurationManager;
	@Inject	private LabelJsonProvider versionLabelJsonProvider;
	
	
	public NodeWithMonitorsInfo getNodeByNameOrNull(String projectName, String nodeName){
		List<NodeWithMonitorsInfo> nodes = getNodes(projectName);
		for (NodeWithMonitorsInfo nodeWithMonitorsInfo : nodes) 
		{
			if (nodeWithMonitorsInfo.name().equals(nodeName)) 
				return nodeWithMonitorsInfo;
		}
		return null;
	}

	public NodeWithMonitorsInfo getNodeByName(String projectName, String nodeName) {
		NodeWithMonitorsInfo $ = getNodeByNameOrNull(projectName, nodeName);
		if ($ == null) {
			throw new IllegalArgumentException("Node " + nodeName + " not found in project " + projectName);
		}
		return $;
	}

	public PeerStatusJsonV2 peer(String peer_key) {
		for (Entry<String, PeerStatusJsonV2> e : peersProjectsStatus.peer_to_projects().entrySet()) {
			if (e.getKey().equals(peer_key)){
				return e.getValue();
			}
		}
		throw new IllegalArgumentException("peer not found " + peer_key);
	}
	public List<PeerStatusJsonV2> peers() {
		return Lists.newArrayList(peersProjectsStatus.peer_to_projects().values());
	}
	
	

	public List<NodeWithMonitorsInfo> getNodes(String projectName) {
		return getNodes(projectName, Constants.ALL_VERSION);
	}

	public List<NodeWithMonitorsInfo> getNodes(String projectName, String versionName) {
		ProjectJson projectJson = configurationManager.getProjectForName(projectName);
		versionName = versionLabelJsonProvider.versionForLabel(versionName, projectName);
		ViewNodesFilter versionFilter = new ViewNodesFilter(versionName, Integer.MAX_VALUE, "", 0);
		Collection<PeerStatusJsonV2> allPeers = peersProjectsStatus.peer_to_projects().values();
		List<NodeWithMonitorsInfo> $ = Lists.newArrayList();
		for (PeerStatusJsonV2 peerStatusJsonV2 : allPeers) {
			ProjectStatus project = peerStatusJsonV2.project_name_to_status().get(projectName);
			if (project == null) {
				continue;
			}
			for (NodeWithMonitorsInfo node : project.nodes_info()) {
				String alias = node.alias();
				if ((versionName.equals(Constants.ALL_VERSION)) || (!versionFilter.filter(node.version(), alias))) {
					if (projectJson.node_discovery_startegy() == NodeDiscoveryStrategy.Configuration && !projectName.equals(Constants.CODEINE_NODES_PROJECT_NAME)) {
						node.tags(findTags(projectJson, node));
					}
					if (projectName.equals(Constants.CODEINE_NODES_PROJECT_NAME)) {
						node.collectors(allCollectorsForInternalProject(peerStatusJsonV2));
					}
					node.peer(peerStatusJsonV2);
					$.add(node);
				} 
			}
		}
		return $;
	}

	private Map<String, CollectorExecutionInfo> allCollectorsForInternalProject(PeerStatusJsonV2 peerStatusJsonV2) {
		Map<String, CollectorExecutionInfo> $ = Maps.newHashMap();
		for (Entry<String, ProjectStatus> projName2Status : peerStatusJsonV2.project_name_to_status().entrySet()) {
			for (NodeWithMonitorsInfo nodeInfo : projName2Status.getValue().nodes_info()) {
				for (Entry<String, CollectorExecutionInfo> colName2Info : nodeInfo.collectors().entrySet()) {
					CollectorExecutionInfo c = colName2Info.getValue();
					String name = projName2Status.getKey() + "|" + nodeInfo.alias() + "|" + c.name();
					CollectorExecutionInfo value = new CollectorExecutionInfo(name, c.type(), c.exit_status(), c.value(), c.execution_duration(), c.start_time());
					$.put(name, value);
				}
			}
		}
		return $;
	}

	private List<String> findTags(ProjectJson projectJson, NodeWithMonitorsInfo node) {
		for (NodeInfo n : projectJson.nodes_info()) {
			if (n.name().equals(node.name())) {
				return n.tags();
			}
		}
		return Lists.newArrayList();
	}

	public List<NodeWithMonitorsInfo> getNodes(String projectName, final List<NodeWithPeerInfo> filterNodes) {
		Predicate<NodeWithMonitorsInfo> predicate = new Predicate<NodeWithMonitorsInfo>() {
			@Override
			public boolean apply(NodeWithMonitorsInfo n){
				for (NodeWithPeerInfo NodeWithPeerInfo : filterNodes) {
					if (NodeWithPeerInfo.name().equals(n.name())){
						return true;
					}
				}
				return false;
			}
		};
		return Lists.newArrayList(Iterables.filter(getNodes(projectName), predicate ));
	}
	
	

	
}
