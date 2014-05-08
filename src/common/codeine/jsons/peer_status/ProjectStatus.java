package codeine.jsons.peer_status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import codeine.api.NodeWithMonitorsInfo;
import codeine.utils.MiscUtils;

import com.google.common.collect.Lists;

public class ProjectStatus {
	
	private static final Logger log = Logger.getLogger(ProjectStatus.class);
	private List<NodeWithMonitorsInfo> nodes_info = Lists.<NodeWithMonitorsInfo>newArrayList();
	private String project_name;
	
	
	public ProjectStatus() {
		super();
	}

	public ProjectStatus(String project_name, List<NodeWithMonitorsInfo> nodes_info) {
		this.project_name = project_name;
		this.nodes_info.addAll(nodes_info);
	}
	public ProjectStatus(String project_name) {
		this(project_name, Lists.<NodeWithMonitorsInfo>newArrayList());
	}
	
	public ProjectStatus(String project_name, NodeWithMonitorsInfo node_info) {
		this(project_name, Lists.newArrayList(node_info));
	}
	
	public List<NodeWithMonitorsInfo> nodes_info() {
		return Collections.unmodifiableList(nodes_info);
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
		synchronized (this) {
			ArrayList<NodeWithMonitorsInfo> nodes = Lists.newArrayList(nodes_info);
			nodes.add(nodeInfo);
			nodes_info = nodes;
		}
	}
	
	public void removeNodeInfo(String node) {
		synchronized (this) {
			ArrayList<NodeWithMonitorsInfo> nodes = Lists.newArrayList(nodes_info);
			for (Iterator<NodeWithMonitorsInfo> iterator = nodes.iterator(); iterator.hasNext();) {
				NodeWithMonitorsInfo nodeWithMonitorsInfo = iterator.next();
				if (MiscUtils.equals(node, nodeWithMonitorsInfo.name())){
					log.info("removed node " + node);
					iterator.remove();
				}
			}
			nodes_info = nodes;
		}
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
