package codeine.servlets.api_servlets;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeine.api.NodeGetter;
import codeine.api.NodeWithMonitorsInfo;
import codeine.model.Constants;
import codeine.servlet.AbstractServlet;

import com.google.inject.Inject;

public class ProjectNodesApiServlet extends AbstractServlet {
	
	@Inject	private NodeGetter nodesGetter;
	
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		String versionName = request.getParameter(Constants.UrlParameters.VERSION_NAME);
		List<NodeWithMonitorsInfo> nodes = nodesGetter.getNodes(projectName,versionName);
		writeResponseJson(response, nodes);
	}
	
	
}
