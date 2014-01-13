package codeine.servlets.api_servlets;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeine.api.NodeInfo;
import codeine.configuration.IConfigurationManager;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.servlet.AbstractServlet;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

public class ProjectTagsApiServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	@Inject	private IConfigurationManager configurationManager;
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		
		ProjectJson project = configurationManager.getProjectForName(projectName);
		Map<String,Integer> map = Maps.newHashMap();
		
		for (NodeInfo node : project.nodes_info()) {
			for (String tag : node.tags()) {
				if (!map.containsKey(tag)) {
					map.put(tag, 1);
				} else {
					map.put(tag, map.get(tag) + 1);
				}
			}
		}
		List<NodeTag> list = Lists.newArrayList();
		for (Entry<String, Integer> e : map.entrySet()) {
			list.add(new NodeTag(e.getKey(), e.getValue()));
		}
		
		writeResponseJson(response, list);
	}
	
	
	public static class NodeTag {
		
		
		
		public NodeTag(String name, int count) {
			super();
			this.name = name;
			this.count = count;
		}
		String name;
		int count;
	}
	

}
