package codeine.servlets.api_servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeine.model.Constants;
import codeine.servlet.AbstractApiServlet;
import codeine.statistics.IMonitorStatistics;

import com.google.inject.Inject;

public class MonitorsStatisticsApiServlet extends AbstractApiServlet {
	
	private @Inject IMonitorStatistics monitorsStatistics;
	private static final long serialVersionUID = 1L;
	
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		writeResponseGzipJson(response, monitorsStatistics.getData(projectName));
	}
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return canReadProject(request);
	}
}
