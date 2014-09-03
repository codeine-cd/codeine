package codeine.servlets.api_servlets.angular;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeine.servlet.AbstractApiServlet;
import codeine.servlet.ManageStatisticsCollector;
import codeine.servlet.ManageStatisticsInfo;

public class ManageStatisticsInfoApiServlet extends AbstractApiServlet {

	private @Inject ManageStatisticsCollector manageStatisticsCollector;
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		ManageStatisticsInfo manageStatisticsInfo = manageStatisticsCollector.getCollected();
		writeResponseJson(response, manageStatisticsInfo);
	}
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return isAdministrator(request);
	}

}
