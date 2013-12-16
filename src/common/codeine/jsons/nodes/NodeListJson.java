package codeine.jsons.nodes;

import java.util.List;

import codeine.api.NodeInfo;

import com.google.common.collect.Lists;

public class NodeListJson {
	
	private List<NodeInfo> nodes = Lists.newArrayList();

	public List<NodeInfo> nodes() {
		return nodes;
	}

	public void addAll(List<NodeInfo> nodes2) {
		nodes.addAll(nodes2);
	}

	@Override
	public String toString() {
		return "NodeListJson [nodes=" + nodes + "]";
	}

	public void add(NodeInfo nodeJson) {
		nodes.add(nodeJson);
	}
	
	
}
