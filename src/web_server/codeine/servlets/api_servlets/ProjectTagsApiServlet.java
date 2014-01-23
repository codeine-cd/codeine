package codeine.servlets.api_servlets;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.api.NodeGetter;
import codeine.api.NodeInfo;
import codeine.api.NodeWithMonitorsInfo;
import codeine.configuration.IConfigurationManager;
import codeine.jsons.nodes.NodeDiscoveryStrategy;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.servlet.AbstractServlet;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

public class ProjectTagsApiServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(ProjectTagsApiServlet.class);
	@Inject	private IConfigurationManager configurationManager;
	@Inject	private NodeGetter nodesGetter;
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		
		ProjectJson project = configurationManager.getProjectForName(projectName);
		Map<String,Integer> map = Maps.newHashMap();
		
		if (project.node_discovery_startegy() == NodeDiscoveryStrategy.Configuration && !projectName.equals(Constants.CODEINE_NODES_PROJECT_NAME)) {
			log.info("getting static tags");
			for (NodeInfo node : project.nodes_info()) {
				updateMapWithNode(map, node);
			}
		}
		else {
			List<NodeWithMonitorsInfo> nodes = nodesGetter.getNodes(projectName);
			for (NodeWithMonitorsInfo nodeWithMonitorsInfo : nodes) {
				updateMapWithNode(map, nodeWithMonitorsInfo);
			}
		}
		List<NodeTag> list = Lists.newArrayList();
		for (Entry<String, Integer> e : map.entrySet()) {
			list.add(new NodeTag(e.getKey(), e.getValue()));
		}
		
		Comparator<NodeTag> c = new Comparator<NodeTag>() {
			@Override
			public int compare(NodeTag o1, NodeTag o2) {
				return o1.name().compareTo(o2.name());
			}
		};
		Collections.sort(list, c);
		
		writeResponseJson(response, list);
	}


	public void updateMapWithNode(Map<String, Integer> map, NodeInfo node) {
		if (node.tags() == null) {
			return;
		}
		for (String tag : node.tags()) {
			if (!map.containsKey(tag)) {
				map.put(tag, 1);
			} else {
				map.put(tag, map.get(tag) + 1);
			}
		}
	}
	
	
	public static class NodeTag {
		public NodeTag(String name, int count) {
			super();
			this.name = name;
			this.count = count;
		}
		
		String name;
		int count;
		
		public String name() {
			return name;
		}
	}
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return canReadProject(request);
	}
}
