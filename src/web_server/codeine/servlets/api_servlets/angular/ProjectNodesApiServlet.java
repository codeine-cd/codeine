package codeine.servlets.api_servlets.angular;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeine.api.NodeGetter;
import codeine.api.NodeWithMonitorsInfo;
import codeine.model.Constants;
import codeine.servlet.AbstractServlet;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class ProjectNodesApiServlet extends AbstractServlet {

	@Inject	private NodeGetter nodesGetter;
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return canReadProject(request);
	}
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		List<NodeWithMonitorsInfo> nodes = nodesGetter.getNodes(projectName,Constants.ALL_VERSION);
		List<NodeWithMonitorsInfoApi> $ = Lists.newArrayList();
		for (NodeWithMonitorsInfo nodeWithMonitorsInfo : nodes) {
			$.add(new NodeWithMonitorsInfoApi(nodeWithMonitorsInfo));
		}
		writeResponseGzipJson(response, $);
	}

}
