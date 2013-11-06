package codeine.jsons.nodes;

import codeine.api.NodeInfo;
import codeine.jsons.peer_status.PeerStatusJsonV2;
import codeine.jsons.peer_status.ProjectStatus;
import codeine.jsons.project.ProjectJson;

public class NodeHelper {

//	public List<NodeJson> getNodes(ProjectJson project, String hostname) {
//		if (project.nodes().isEmpty()){
//			return Lists.newArrayList(new NodeJson(hostname));
//		}
//		List<NodeJson> $ = Lists.newArrayList();
//		for (NodeJson node : project.nodes()) {
//			if (InetUtils.nameWithoutPort(node.name()).equals(hostname)){
//				$.add(node);
//			}
//		}
//		return $;
//	}
//	public List<NodeJson> getNodesInPeer(ProjectJson project, String hostname) {
//		if (project.nodes().isEmpty()){
//			if (project.node_discovery_startegy() == NodeDiscoveryStrategy.Script)
//			{
//				return Lists.newArrayList();
//			}
//			else
//			{
//				return Lists.newArrayList(new NodeJson(hostname));
//			}
//		}
//		List<NodeJson> $ = Lists.newArrayList();
//		for (NodeJson node : project.nodes()) {
//			if (InetUtils.nameWithoutPort(node.name()).equals(hostname)){
//				$.add(node);
//			}
//		}
//		return $;
//	}
	public String getVersionOrNull(ProjectStatus projectStatus, NodeInfo node) {
		return projectStatus.getVersionOrNull(node.name());
	}
	public String getVersionOrNull(PeerStatusJsonV2 projectStatusList, ProjectJson project, NodeInfo node) {
		return getVersionOrNull(projectStatusList.project_name_to_status().get(project.name()), node);
	}
}
