package codeine.servlets.api_servlets.angular;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeine.api.NodeGetter;
import codeine.api.NodeWithMonitorsInfo;
import codeine.configuration.Links;
import codeine.model.Constants;
import codeine.servlet.AbstractServlet;
import codeine.utils.network.HttpUtils;

import com.google.inject.Inject;

public class MonitorStatusApiServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	@Inject	private NodeGetter nodesGetter;
	@Inject	private Links links;
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return canReadProject(request);
	}
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		String nodeName = request.getParameter(Constants.UrlParameters.NODE);
		String monitorName = request.getParameter(Constants.UrlParameters.MONITOR);
		NodeWithMonitorsInfo node = nodesGetter.getNodeByName(projectName, nodeName);
		String peerMonitorResultLink = links.getPeerMonitorResultLink(node.peer_address(), projectName, monitorName, nodeName);
		String encodeOutput = HttpUtils.encodeHTML(HttpUtils.doGET(peerMonitorResultLink,null));
		writeResponseGzipJson(response, new MonitorExecutionResult(encodeOutput));
	}
	
	

}
