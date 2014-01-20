package codeine.servlets.api_servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.api.NodeWithMonitorsInfo;
import codeine.db.IStatusDatabaseConnector;
import codeine.jsons.peer_status.PeerStatusJsonV2;
import codeine.jsons.peer_status.ProjectStatus;
import codeine.servlet.AbstractServlet;

import com.google.inject.Inject;


public class ReporterServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(ReporterServlet.class);

	@Inject private IStatusDatabaseConnector databaseConnector;

	@Override
	protected void myPost(HttpServletRequest req, HttpServletResponse resp){
		NodeWithMonitorsInfo nodeWithMonitorsInfo = readBodyJson(req, NodeWithMonitorsInfo.class);
		log.info("Recieved status " + nodeWithMonitorsInfo);
		PeerStatusJsonV2 json = new PeerStatusJsonV2(nodeWithMonitorsInfo.projectName() + "_" + nodeWithMonitorsInfo.name(), createProjectStatus(nodeWithMonitorsInfo));
		log.info("Pushing peer report " + json);
		databaseConnector.putReplaceStatus(json);
	}

	private ProjectStatus createProjectStatus(NodeWithMonitorsInfo nodeWithMonitorsInfo) {
		ProjectStatus projectStatus = new ProjectStatus(nodeWithMonitorsInfo.projectName(),nodeWithMonitorsInfo);
		return projectStatus;
	}
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return true;
	}
}

