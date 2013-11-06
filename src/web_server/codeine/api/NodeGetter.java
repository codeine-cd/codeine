package codeine.api;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import codeine.jsons.labels.LabelJsonProvider;
import codeine.jsons.peer_status.PeerStatusJsonV2;
import codeine.jsons.peer_status.PeersProjectsStatus;
import codeine.jsons.peer_status.ProjectStatus;
import codeine.model.Constants;
import codeine.version.ViewNodesFilter;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

public class NodeGetter {
	@Inject	private PeersProjectsStatus peersProjectsStatus;
	@Inject	private LabelJsonProvider versionLabelJsonProvider;
	
//	Map<String, String> nodeMap = projectStatus.monitor_to_status().get(nodeJson.name());
	public NodeWithMonitorsInfo getNodeByName(String projectName, String nodeName, String version){
		return null;
	}

	public List<PeerStatusJsonV2> peers() {
		return Lists.newArrayList(peersProjectsStatus.peer_to_projects().values());
	}

	public List<NodeWithMonitorsInfo> getNodes(String projectName) {
		return getNodes(projectName, Constants.ALL_VERSION);
	}

	public List<NodeWithMonitorsInfo> getNodes(String projectName, String versionName) {
		versionName = versionLabelJsonProvider.versionForLabel(versionName, projectName);
		ViewNodesFilter versionFilter = new ViewNodesFilter(versionName, Integer.MAX_VALUE, "", 0);
		
		Collection<PeerStatusJsonV2> allPeers = peersProjectsStatus.peer_to_projects().values();
		List<NodeWithMonitorsInfo> $ = Lists.newArrayList();
		for (PeerStatusJsonV2 peerStatusJsonV2 : allPeers) {
			ProjectStatus project = peerStatusJsonV2.project_name_to_status().get(projectName);
			if (project == null) 
				continue;
			for (NodeWithMonitorsInfo node : getNodesInfo(project, peerStatusJsonV2, projectName)) {
				String alias = node.alias();
				if ((versionName.equals(Constants.ALL_VERSION)) || (!versionFilter.filter(node.version(), alias))) {
					$.add(node);
				} 
			}
		}
		return $;
	}

	public List<NodeWithMonitorsInfo> getNodes(String projectName, final List<NodeDataJson> filterNodes) {
		Predicate<NodeWithMonitorsInfo> predicate = new Predicate<NodeWithMonitorsInfo>() {
			@Override
			public boolean apply(NodeWithMonitorsInfo n){
				for (NodeDataJson nodeDataJson : filterNodes) {
					if (nodeDataJson.node_name().equals(n.name())){
						return true;
					}
				}
				return false;
			}
		};
		return Lists.newArrayList(Iterables.filter(getNodes(projectName), predicate ));
	}
	
	//TODO can be remove after peer upgrade to 710
	private List<NodeWithMonitorsInfo> getNodesInfo(ProjectStatus project, PeerStatusJsonV2 peerStatusJsonV2, String projectName) {
		List<NodeWithMonitorsInfo> nodesInfo = project.nodesInfo();
		if (nodesInfo.isEmpty()){
			for (Entry<String, Map<String, String>> nodeInfoOld : project.nodesInfoOld().entrySet()) {
				Map<String, String> value = nodeInfoOld.getValue();
				Map<String, MonitorInfo> monitors = Maps.newHashMap();
				for (Entry<String, String> e : value.entrySet()) {
					monitors.put(e.getKey(), new MonitorInfo(e.getKey(), e.getKey(), e.getValue()));
				}
				nodesInfo.add(new NodeWithMonitorsInfo(peerStatusJsonV2.host_port(),nodeInfoOld.getKey(), nodeInfoOld.getKey(), projectName, monitors));
			}
		}
		return nodesInfo;
	}

	
}
