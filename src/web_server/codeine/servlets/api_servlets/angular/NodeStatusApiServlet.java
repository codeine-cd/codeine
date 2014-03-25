package codeine.servlets.api_servlets.angular;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeine.api.NodeGetter;
import codeine.model.Constants;
import codeine.servlet.AbstractServlet;

import com.google.inject.Inject;

public class NodeStatusApiServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	@Inject	private NodeGetter nodesGetter;
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return canReadProject(request);
	}
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		String nodeName = request.getParameter(Constants.UrlParameters.NODE);
		NodeWithMonitorsInfoApi node = new NodeWithMonitorsInfoApi(nodesGetter.getNodeByName(projectName, nodeName));
		writeResponseGzipJson(response, node);
	}

}
