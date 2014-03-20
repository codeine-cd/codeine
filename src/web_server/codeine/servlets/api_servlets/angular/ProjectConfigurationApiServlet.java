package codeine.servlets.api_servlets.angular;


import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.ConfigurationManagerServer;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.servlet.AbstractServlet;

public class ProjectConfigurationApiServlet extends AbstractServlet {

	
	private static final Logger log = Logger.getLogger(ProjectConfigurationApiServlet.class);

	private static final long serialVersionUID = 1L;

	@Inject private ConfigurationManagerServer configurationManager;
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return canConfigureProject(request);
	}
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		writeResponseJson(response, configurationManager.getProjectForName(request.getParameter(Constants.UrlParameters.PROJECT_NAME)));
	}
	
	
	
	@Override
	protected void myPut(HttpServletRequest request, HttpServletResponse resp) {
		ProjectJson projectJson = readBodyJson(request, ProjectJson.class);
		log.info("Updating configuration of " + projectJson.name() + ", new configuration is " + projectJson);
		configurationManager.updateProject(projectJson);
		writeResponseJson(resp,projectJson);
	}

}
