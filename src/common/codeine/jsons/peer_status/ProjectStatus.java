package codeine.jsons.peer_status;

import java.util.List;

import codeine.api.NodeWithMonitorsInfo;
import codeine.utils.MiscUtils;

import com.google.common.collect.Lists;

public class ProjectStatus {
	
	private List<NodeWithMonitorsInfo> nodes_info = Lists.newArrayList();
	private String project_name;
	
	
	public ProjectStatus() {
		super();
	}

	public ProjectStatus(String project_name, List<NodeWithMonitorsInfo> nodes_info) {
		this.project_name = project_name;
		this.nodes_info = nodes_info;
		
	}
	
	public ProjectStatus(String project_name, NodeWithMonitorsInfo node_info) {
		this.project_name = project_name;
		this.nodes_info.add(node_info);
	}
	
	public List<NodeWithMonitorsInfo> nodes_info() {
		return nodes_info;
	}


	@Override
	public String toString() {
		return "ProjectStatus [nodes_info=" + nodes_info + ", project_name=" + project_name + "]";
	}

	public String getVersionOrNull(String nodeName) {
		List<NodeWithMonitorsInfo> nodes = Lists.newArrayList(nodes_info());
		for (NodeWithMonitorsInfo nodeInfo : nodes) {
			if (MiscUtils.equals(nodeInfo.name(), nodeName)){
				return nodeInfo.version();
			}
		}
		return null;
	}

	public NodeWithMonitorsInfo nodeInfoOrNull(String nodeName) {
		for (NodeWithMonitorsInfo n : nodes_info()) {
			if (MiscUtils.equals(n.name(), nodeName)){
				return n;
			}
		}
		return null;
	}

	public void addNodeInfo(NodeWithMonitorsInfo nodeInfo) {
		nodes_info.add(nodeInfo);
	}

	public String project_name() {
		return project_name;
	}



	public void updateNodesWithPeer(PeerStatusJsonV2 peerStatusJsonV2) {
		for (NodeWithMonitorsInfo n : nodes_info) {
			n.peer(peerStatusJsonV2);
		}

	}

	
	
	
}
