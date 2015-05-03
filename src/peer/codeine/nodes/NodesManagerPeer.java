package codeine.nodes;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.api.NodeInfo;
import codeine.configuration.PathHelper;
import codeine.jsons.nodes.NodeListJson;
import codeine.jsons.nodes.NodesManager;
import codeine.jsons.project.ProjectJson;
import codeine.utils.FilesUtils;
import codeine.utils.network.InetUtils;

import com.google.common.collect.Maps;

public class NodesManagerPeer implements NodesManager {

	private static final Logger log = Logger.getLogger(NodesManagerPeer.class);

	@Inject
	private NodeScriptDiscovery nodeScriptDiscovery;

	private Map<String, NodeListJson> projectToNode = Maps.newConcurrentMap();
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
		log.info("init nodes in peer for project " + projectJson.name());
		String dir = pathHelper.getProjectDir(projectJson.name());
		FilesUtils.mkdirs(dir);
		switch (projectJson.node_discovery_startegy()) {
		case Configuration: {
			List<NodeInfo> nodes = projectJson.nodes_info();
			NodeListJson nodes2 = new NodeListJson();
			for (NodeInfo nodeJson : nodes) {
				String configuredHostName = InetUtils.nameWithoutPort(nodeJson.name());
				if (isLocalHostNameMatch(configuredHostName)){
					nodes2.add(nodeJson);
				}
			}
			projectToNode.put(projectJson.name(), nodes2);
			log.info("Configuration nodes are " + nodes2);
			break;
		}
		case Script: {
			NodeListJson nodes = nodeScriptDiscovery.get(projectJson);
			projectToNode.put(projectJson.name(), nodes);
			log.info("Script nodes are " + nodes);
			break;
		}
		case Reporter: {
			log.info("project configured as reporter, no nodes");
			break;
		}
		default:
			throw new UnsupportedOperationException("for value " + projectJson.node_discovery_startegy());
		}
	}
	private boolean isLocalHostNameMatch(String configuredHostName) {
		InetAddress localHost = InetUtils.getLocalHost();
		return configuredHostName.equalsIgnoreCase(localHost.getHostName())
				|| configuredHostName.equalsIgnoreCase(localHost.getCanonicalHostName());
	}

}
