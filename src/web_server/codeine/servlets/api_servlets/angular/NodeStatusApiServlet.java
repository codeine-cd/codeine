package codeine.servlets.api_servlets.angular;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.api.MonitorStatusInfo;
import codeine.api.NodeGetter;
import codeine.api.NodeInfo;
import codeine.api.NodeWithMonitorsInfo;
import codeine.configuration.IConfigurationManager;
import codeine.jsons.peer_status.PeerStatusJsonV2;
import codeine.jsons.peer_status.PeerStatusString;
import codeine.jsons.peer_status.ProjectStatus;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.servlet.AbstractApiServlet;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

public class NodeStatusApiServlet extends AbstractApiServlet {

	private static final Logger log = Logger
			.getLogger(NodeStatusApiServlet.class);
	private static final long serialVersionUID = 1L;
	@Inject	private NodeGetter nodesGetter;
	@Inject	private IConfigurationManager configurationManager;
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return canReadProject(request);
	}
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		String nodeName = request.getParameter(Constants.UrlParameters.NODE);
		NodeWithMonitorsInfo nodeByNameOrNull = nodesGetter.getNodeByNameOrNull(projectName, nodeName);
		NodeWithMonitorsInfoApi node = null;
		if (nodeByNameOrNull != null) {
			node = new NodeWithMonitorsInfoApi(nodeByNameOrNull);
		}
		else {
			ProjectJson projectJson = configurationManager.getProjectForName(projectName);
			for (NodeInfo nodeInfo : projectJson.nodes_info()) {
				Map<String, MonitorStatusInfo> monitors = Maps.newHashMap();
				ProjectStatus projectStatus = new ProjectStatus();
				PeerStatusJsonV2 peer = new PeerStatusJsonV2("", projectStatus);
				peer.status(PeerStatusString.Offline);
				NodeWithMonitorsInfoApi nodeStatusInfo = new NodeWithMonitorsInfoApi(new NodeWithMonitorsInfo(
						peer, nodeInfo.name(), nodeInfo.alias(), projectJson.name(), monitors));
				log.info("offline node " + nodeStatusInfo);
				node = nodeStatusInfo;
				break;
			}
		}
		if (null == node){
			throw new IllegalArgumentException("node not found " + nodeName);
		}
		writeResponseGzipJson(response, node);
	}

}
