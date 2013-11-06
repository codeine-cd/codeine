package codeine.jsons.peer_status;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import codeine.api.NodeWithMonitorsInfo;
import codeine.model.Constants;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ProjectStatus {
	
	//TODO can be remove after peer upgrade to 710
	private Map<String,Map<String,String>> monitor_to_status = Maps.newHashMap();//node->monitor->status
	private List<NodeWithMonitorsInfo> nodesInfo = Lists.newArrayList();
	private String projectName;
	
	
	public ProjectStatus() {
		super();
	}

	public ProjectStatus(String projectName, List<NodeWithMonitorsInfo> nodesInfo) {
		this.projectName = projectName;
		this.nodesInfo = nodesInfo;
		
	}
	
	public List<NodeWithMonitorsInfo> nodesInfo() {
		return nodesInfo;
	}
	//TODO can be remove after peer upgrade to 710
	public Map<String,Map<String,String>> nodesInfoOld() {
		return monitor_to_status;
	}

	@Override
	public String toString() {
		return "ProjectStatus [monitor_to_status=" + monitor_to_status + ", nodesInfo=" + nodesInfo + ", projectName="
				+ projectName + "]";
	}

	public String getVersionOrNull(String nodeName) {
		List<NodeWithMonitorsInfo> nodes = Lists.newArrayList(nodesInfo());
		for (NodeWithMonitorsInfo nodeInfo : nodes) {
			if (nodeInfo.name().equals(nodeName)){
				return nodeInfo.version();
			}
		}
		for (Entry<String, Map<String, String>> e : nodesInfoOld().entrySet()) {
			if (e.getKey().equals(nodeName)){
				return e.getValue().get(Constants.VERSION);
			}
		}
		return null;
	}

	public NodeWithMonitorsInfo nodeInfoOrNull(String nodeName) {
		for (NodeWithMonitorsInfo n : nodesInfo) {
			if (n.name().equals(nodeName)){
				return n;
			}
		}
		return null;
	}

	public void addNodeInfo(NodeWithMonitorsInfo nodeInfo) {
		nodesInfo.add(nodeInfo);
	}

	
	
	
}
