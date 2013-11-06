package codeine.jsons.peer_status;

import java.util.List;

import codeine.api.NodeInfo;

import com.google.common.collect.Lists;

public class DiscoveredProjectJson {
	
	private String project_name;
	private List<NodeInfo> nodes = Lists.newArrayList();
	
	public DiscoveredProjectJson(String project_name, List<NodeInfo> nodes) {
		this.project_name = project_name;
		this.nodes = nodes;
	}

	public String project_name() {
		return project_name;
	}

	public List<NodeInfo> nodes() {
		return nodes;
	}
}
