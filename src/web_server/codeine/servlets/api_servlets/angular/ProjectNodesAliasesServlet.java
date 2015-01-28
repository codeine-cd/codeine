package codeine.servlets.api_servlets.angular;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeine.api.NodeGetter;
import codeine.api.NodeWithMonitorsInfo;
import codeine.model.Constants;
import codeine.servlet.AbstractApiServlet;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class ProjectNodesAliasesServlet extends AbstractApiServlet {

	private static final long serialVersionUID = 1L;
	@Inject	private NodeGetter nodesGetter;
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return canReadProject(request);
	}
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		String projectName = getParameter(request, Constants.UrlParameters.PROJECT_NAME);
		List<NodeWithMonitorsInfo> nodes = nodesGetter.getNodes(projectName,Constants.ALL_VERSION);
		List<String> $ = Lists.newArrayList();
		for (NodeWithMonitorsInfo nodeWithMonitorsInfo : nodes) {
			$.add(nodeWithMonitorsInfo.alias());
		}
		writeResponseGzipJson($, request, response);
	}

}
