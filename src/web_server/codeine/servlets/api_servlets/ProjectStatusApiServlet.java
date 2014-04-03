package codeine.servlets.api_servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeine.api.NodeAggregator;
import codeine.model.Constants;
import codeine.servlet.AbstractApiServlet;

import com.google.inject.Inject;

public class ProjectStatusApiServlet extends AbstractApiServlet {
	
	@Inject	private NodeAggregator aggregator;
	
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		writeResponseJson(response, aggregator.aggregate(projectName));
	}
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return canReadProject(request);
	}
}
