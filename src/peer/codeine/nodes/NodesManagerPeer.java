package codeine.nodes;

import java.util.Map;

import javax.inject.Inject;

import codeine.api.NodeInfo;
import codeine.configuration.PathHelper;
import codeine.jsons.nodes.NodeListJson;
import codeine.jsons.nodes.NodesManager;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.utils.JsonFileUtils;
import codeine.utils.network.InetUtils;

import com.google.common.collect.Maps;

public class NodesManagerPeer extends NodesManager {

	@Inject
	private NodeScriptDiscovery nodeScriptDiscovery;

	private Map<String, NodeListJson> projectToNode = Maps.newHashMap();
	@Inject
	private JsonFileUtils jsonFileUtils;
	@Inject
	private PathHelper pathHelper;
	
	@Override
	public NodeListJson nodesOf(ProjectJson projectJson){
		NodeListJson nodeListJson = projectToNode.get(projectJson.name());
		if (null == nodeListJson){
			return new NodeListJson();
		}
		return nodeListJson;
	}
	@Override
	public void init(ProjectJson projectJson){
		switch (projectJson.node_discovery_startegy()) {
		case Configuration: {
			String file2 = pathHelper.getProjectsDir() + "/" + projectJson.name() + "/" + Constants.NODES_CONF_FILE;
			NodeListJson nodes = jsonFileUtils.getConfFromFile(file2, NodeListJson.class);
			if (null == nodes){
				throw new IllegalArgumentException("bad configuration for project " + projectJson.name() + " in file " + file2);
			}
			NodeListJson nodes2 = new NodeListJson();
			for (NodeInfo nodeJson : nodes.nodes()) {
				if (InetUtils.nameWithoutPort(nodeJson.name()).equals(InetUtils.getLocalHost().getHostName())){
					nodes2.add(nodeJson);
				}
			}
			projectToNode.put(projectJson.name(), nodes2);
			break;
		}
		case Script: {
			projectToNode.put(projectJson.name(), nodeScriptDiscovery.get(projectJson.name()));
			break;
		}
		default:
			throw new UnsupportedOperationException("for value " + projectJson.node_discovery_startegy());
		}
	}

}
