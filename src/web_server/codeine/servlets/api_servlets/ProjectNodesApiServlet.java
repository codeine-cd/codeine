package codeine.servlets.api_servlets;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeine.api.NodeGetter;
import codeine.configuration.IConfigurationManager;
import codeine.configuration.Links;
import codeine.model.Constants;
import codeine.servlet.AbstractServlet;
import codeine.servlet.NodeTemplate;
import codeine.servlets.front_end.ProjectsStatusUtils;

import com.google.inject.Inject;

public class ProjectNodesApiServlet extends AbstractServlet {
	
	@Inject	private NodeGetter nodesGetter;
	@Inject	private Links links;
	@Inject	private IConfigurationManager configurationManager;
	
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		String versionName = request.getParameter(Constants.UrlParameters.VERSION_NAME);
		List<NodeTemplate> nodes = ProjectsStatusUtils.getVersionsNodes(projectName, versionName, configurationManager.getProjectForName(projectName), nodesGetter, links);
		writeResponseJson(response, nodes);
	}
	
	
}
