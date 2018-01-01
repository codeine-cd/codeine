package codeine.servlets.api_servlets.projects;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.ConfigurationManagerServer;
import codeine.api.NodeAggregator;
import codeine.jsons.project.CodeineProject;
import codeine.jsons.project.ProjectJson;
import codeine.permissions.IUserWithPermissions;
import codeine.permissions.UserPermissionsGetter;
import codeine.plugins.AfterProjectModifyPlugin;
import codeine.plugins.AfterProjectModifyPlugin.StatusChange;
import codeine.servlet.AbstractApiServlet;
import codeine.utils.JsonUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class ProjectsListApiServlet extends AbstractApiServlet
{
	private static final Logger log = Logger.getLogger(ProjectsListApiServlet.class);
	private static final long serialVersionUID = 1L;
	@Inject private ConfigurationManagerServer configurationManager;
	@Inject private UserPermissionsGetter permissionsManager;
	@Inject	private NodeAggregator aggregator;
	@Inject private AfterProjectModifyPlugin afterProjectModifyPlugin;
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		String query = getParameter(request, "projectSearch");
		List<ProjectJson> configuredProjects = filter(configurationManager.getConfiguredProjects(), query);
		
		Comparator<ProjectJson> c = Comparator.comparing(ProjectJson::name);
		Collections.sort(configuredProjects, c);
		
		List<CodeineProject> projects = Lists.newArrayList();
		IUserWithPermissions user = permissionsManager.user(request);
		for (ProjectJson projectJson : configuredProjects) {
			if (user.canRead(projectJson.name())){
				try {
					projects.add(new CodeineProject(projectJson.name(), aggregator.count(projectJson.name()), projectJson.description()));
				} catch (Exception e) {
					log.error("failed to add project " + projectJson.name(), e);
				}
			}
		}
		writeResponseJson(response, projects);
	}
	
	@Override
	protected void myPost(HttpServletRequest request, HttpServletResponse response) {
		try {
			CreateNewProjectJson newProjectParamsJson = readBodyJson(request, CreateNewProjectJson.class);
			log.info("creating project " + newProjectParamsJson);
			ProjectJson newProject = new ProjectJson();
			if (newProjectParamsJson.type == NewProjectType.Copy) {
				ProjectJson projectForCopy = configurationManager.getProjectForName(newProjectParamsJson.selected_project);
				newProject = JsonUtils.cloneJson(projectForCopy, ProjectJson.class);
			}
			newProject.name(newProjectParamsJson.project_name);
			configurationManager.createNewProject(newProject);
			afterProjectModifyPlugin.call(newProject, StatusChange.add, getUser(request).user().username());
		} catch (Exception e) {
			throw new IllegalArgumentException(e);  
		}	
	}
	
	private List<ProjectJson> filter(List<ProjectJson> configuredProjects, final String query) {
		if (null == query){
			return configuredProjects;
		}
		Predicate<ProjectJson> type = project -> (project.name().toLowerCase().contains(query.toLowerCase()));
		return Lists.newArrayList(Iterables.filter(configuredProjects, type));
	}
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		if (request.getMethod().equals("POST")) {
			if (!isAdministrator(request)) {
				log.info("User can not define new project");
				return false;
			}
			return true;
		}
		return true;
	}
}
