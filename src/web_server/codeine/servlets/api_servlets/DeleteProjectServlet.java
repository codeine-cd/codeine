package codeine.servlets.api_servlets;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;

import codeine.ConfigurationManagerServer;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.servlet.AbstractServlet;
import codeine.servlet.PermissionsManager;
import codeine.utils.JsonUtils;

public class DeleteProjectServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(DeleteProjectServlet.class);
	private @Inject PermissionsManager permissionsManager;
	private @Inject ConfigurationManagerServer configurationManager;
	
	@Override
	protected void myDelete(HttpServletRequest request, HttpServletResponse response) {
		String user = permissionsManager.user(request);
		if (!permissionsManager.isAdministrator(request)) {
			log.info("Non admin user (" + user + ") cannot delete project");
			response.setStatus(HttpStatus.FORBIDDEN_403);
			return;
		}
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		ProjectJson projectToDelete = JsonUtils.cloneJson(configurationManager.getProjectForName(projectName), ProjectJson.class);
		configurationManager.deleteProject(projectToDelete);
		log.info("Project " + projectToDelete.name() + " was deleted by user " + user);
		getWriter(response).write("{}");
	}

	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return permissionsManager.isAdministrator(request);
	}
}
