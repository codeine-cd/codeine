package codeine.servlets.api_servlets.angular;


import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.ConfigurationManagerServer;
import codeine.jsons.global.ExperimentalConfJsonStore;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.permissions.IUserWithPermissions;
import codeine.permissions.UserPermissionsGetter;
import codeine.servlet.AbstractApiServlet;
import codeine.utils.JsonUtils;
import codeine.utils.os_process.ProcessExecuter.ProcessExecuterBuilder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ProjectConfigurationApiServlet extends AbstractApiServlet {

	
	private static final Logger log = Logger.getLogger(ProjectConfigurationApiServlet.class);

	private static final long serialVersionUID = 1L;

	@Inject private ConfigurationManagerServer configurationManager;
	@Inject private UserPermissionsGetter permissionsManager;
	@Inject private ExperimentalConfJsonStore experimentalConfJsonStore;
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		if (request.getMethod().equals("DELETE")) {
			return permissionsManager.user(request).isAdministrator();	
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
		if (null != experimentalConfJsonStore.get().after_project_modify_plugin()) {
			log.info("calling after_project_modify_plugin for project " + projectJson.name());
			List<String> cmd = Lists.newArrayList(experimentalConfJsonStore.get().after_project_modify_plugin());
			Map<String, String> env = Maps.newHashMap();
			boolean exists = configurationManager.updateProject(projectJson);
			env.put(Constants.EXECUTION_ENV_PROJECT_EXISTS, String.valueOf(exists));
			env.put(Constants.EXECUTION_ENV_PROJECT_NAME, projectJson.name());
			new ProcessExecuterBuilder(cmd).timeoutInMinutes(2).env(env).build().execute();
		}
		writeResponseJson(resp,projectJson);
	}
	
	@Override
	protected void myDelete(HttpServletRequest request, HttpServletResponse response) {
		IUserWithPermissions user = permissionsManager.user(request);
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		ProjectJson projectToDelete = JsonUtils.cloneJson(configurationManager.getProjectForName(projectName), ProjectJson.class);
		configurationManager.deleteProject(projectToDelete);
		log.info("Project " + projectToDelete.name() + " was deleted by user " + user);
		getWriter(response).write("{}");
	}

}
