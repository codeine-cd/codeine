package codeine.servlets.api_servlets.angular;


import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.ConfigurationManagerServer;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.permissions.IUserWithPermissions;
import codeine.permissions.UserPermissionsGetter;
import codeine.plugins.AfterProjectModifyPlugin;
import codeine.plugins.AfterProjectModifyPlugin.StatusChange;
import codeine.servlet.AbstractApiServlet;
import codeine.utils.JsonUtils;
import codeine.utils.MiscUtils;
import codeine.utils.exceptions.UnAuthorizedException;

public class ProjectConfigurationApiServlet extends AbstractApiServlet {

	
	private static final Logger log = Logger.getLogger(ProjectConfigurationApiServlet.class);
	private static final long serialVersionUID = 1L;

	@Inject private ConfigurationManagerServer configurationManager;
	@Inject private UserPermissionsGetter permissionsManager;
	@Inject private AfterProjectModifyPlugin afterProjectModifyPlugin;
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		if (request.getMethod().equals("DELETE")) {
			return permissionsManager.user(request).isAdministrator();	
		}
		if (request.getMethod().equals("PUT")) {
			return canConfigureProject(request);	
		}
		if (request.getMethod().equals("POST")) {
			return canConfigureProject(request);	
		}
		return canReadProject(request);
	}
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		writeResponseJson(response, configurationManager.getProjectForName(getParameter(request, Constants.UrlParameters.PROJECT_NAME)));
	}
	
	
	@Override
	protected void myPut(HttpServletRequest request, HttpServletResponse resp) {
		ProjectJson projectJson = readBodyJson(request, ProjectJson.class);
		log.info("Updating configuration of " + projectJson.name() + ", new configuration is " + projectJson);
		ProjectJson currentProject = configurationManager.getProjectForName(projectJson.name());
		if (null != currentProject && !isAdministrator(request) && 
				(!MiscUtils.equals(currentProject.nodes_discovery_script(), projectJson.nodes_discovery_script()) || !MiscUtils.equals(currentProject.node_discovery_startegy(), projectJson.node_discovery_startegy()))) {
			log.warn("user tried to change discovery script in " + projectJson.name() + " user " + getUser(request).user().username());
			throw new UnAuthorizedException("only admin can change discovery script");
		}
		boolean exists = configurationManager.updateProject(projectJson);
		afterProjectModifyPlugin.call(projectJson, exists ? StatusChange.modify : StatusChange.add, getUser(request).user().username());
		writeResponseJson(resp,projectJson);
	}

	@Override
	protected void myDelete(HttpServletRequest request, HttpServletResponse response) {
		log.info("got delete request");
		IUserWithPermissions user = permissionsManager.user(request);
		String projectName = getParameter(request, Constants.UrlParameters.PROJECT_NAME);
		log.info("project " + projectName + " user " + user.user().username());
		ProjectJson projectToDelete = JsonUtils.cloneJson(configurationManager.getProjectForName(projectName), ProjectJson.class);
		configurationManager.deleteProject(projectToDelete);
		log.info("Project " + projectToDelete.name() + " was deleted by user " + user);
		afterProjectModifyPlugin.call(projectToDelete, StatusChange.remove, getUser(request).user().username());
		getWriter(response).write("{}");
	}
	
	@Override
	protected void myPost(HttpServletRequest request, HttpServletResponse response) {
		log.info("got post (reload) request");
		IUserWithPermissions user = permissionsManager.user(request);
		String projectName = getParameter(request, Constants.UrlParameters.PROJECT_NAME);
		log.info("reloading project " + projectName + " user " + user.user().username());
		ProjectJson projectJson = configurationManager.reloadProject(projectName);
		writeResponseJson(response, projectJson);
	}

}
