package codeine.servlets.api_servlets.angular;


import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.ConfigurationManagerServer;
import codeine.jsons.auth.IUserPermissions;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.servlet.AbstractServlet;
import codeine.servlet.PermissionsManager;
import codeine.utils.JsonUtils;

public class ProjectConfigurationApiServlet extends AbstractServlet {

	
	private static final Logger log = Logger.getLogger(ProjectConfigurationApiServlet.class);

	private static final long serialVersionUID = 1L;

	@Inject private ConfigurationManagerServer configurationManager;
	@Inject private PermissionsManager permissionsManager;
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		if (request.getMethod().equals("DELETE")) {
			return permissionsManager.isAdministrator(request);	
		}
		if (request.getMethod().equals("PUT")) {
			return canConfigureProject(request);	
		}
		return canReadProject(request);
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
	
	@Override
	protected void myDelete(HttpServletRequest request, HttpServletResponse response) {
		IUserPermissions user = permissionsManager.user(request);
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		ProjectJson projectToDelete = JsonUtils.cloneJson(configurationManager.getProjectForName(projectName), ProjectJson.class);
		configurationManager.deleteProject(projectToDelete);
		log.info("Project " + projectToDelete.name() + " was deleted by user " + user);
		getWriter(response).write("{}");
	}

}
