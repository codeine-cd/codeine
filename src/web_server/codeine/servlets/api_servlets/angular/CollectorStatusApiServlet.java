package codeine.servlets.api_servlets.angular;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.api.NodeGetter;
import codeine.api.NodeWithMonitorsInfo;
import codeine.configuration.Links;
import codeine.jsons.collectors.CollectorExecutionInfoWithResult;
import codeine.model.Constants;
import codeine.servlet.AbstractApiServlet;
import codeine.utils.network.HttpUtils;

import com.google.gson.Gson;
import com.google.inject.Inject;

public class CollectorStatusApiServlet extends AbstractApiServlet {

	private static final Logger log = Logger
			.getLogger(CollectorStatusApiServlet.class);
	private static final long serialVersionUID = 1L;
	@Inject	private NodeGetter nodesGetter;
	@Inject	private Links links;
	@Inject	private Gson gson;
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return canReadProject(request);
	}
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		CollectorExecutionInfoWithResult collectorResult = null;
		try {
			String projectName = getParameter(request, Constants.UrlParameters.PROJECT_NAME);
			String nodeName = getParameter(request, Constants.UrlParameters.NODE);
			String collectorName = getParameter(request, Constants.UrlParameters.COLLECTOR);
			NodeWithMonitorsInfo node = nodesGetter.getNodeByNameOrNull(projectName, nodeName);
			String peerCollectorResultLink = links.getPeerCollectorResultLink(node.peer_address(), projectName, collectorName, nodeName);
			log.info("accessing url " + peerCollectorResultLink);
			String outputFromPeer = HttpUtils.doGET(peerCollectorResultLink,null, HttpUtils.MEDIUM_READ_TIMEOUT_MILLI);
			//		String encodeOutput = HttpUtils.encodeHTML(outputFromPeer);
			collectorResult = gson.fromJson(outputFromPeer, CollectorExecutionInfoWithResult.class);
		} catch (Exception e) {
			log.error("failed to get collector output", e);
			collectorResult = new CollectorExecutionInfoWithResult(null, null);
		}
		writeResponseGzipJson(collectorResult, request, response);
	}
	
	

}
