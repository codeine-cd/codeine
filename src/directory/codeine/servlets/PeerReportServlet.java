package codeine.servlets;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.db.IStatusDatabaseConnector;
import codeine.jsons.peer_status.PeerStatusJsonV2;
import codeine.servlet.AbstractServlet;

public class PeerReportServlet extends AbstractServlet {

	private static final Logger log = Logger.getLogger(PeerReportServlet.class);
	private static final long serialVersionUID = 1L;
	@Inject private IStatusDatabaseConnector databaseConnector;
	
	@Override
	protected void myPost(HttpServletRequest request, HttpServletResponse resp){
		PeerStatusJsonV2 json = readBodyJson(request, PeerStatusJsonV2.class);
		log.info("pushing peer report " + json);
		databaseConnector.putReplaceStatus(json);
	}

	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return true;
	}

}
