package codeine.servlets.api_servlets;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.ConfigurationManagerServer;
import codeine.api.NodeAggregator;
import codeine.api.VersionItemInfo;
import codeine.jsons.project.CodeineProject;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.servlet.AbstractApiServlet;
import codeine.servlet.PermissionsManager;
import codeine.servlets.front_end.NewProjectServlet.CreateNewProjectJson;
import codeine.servlets.front_end.NewProjectServlet.NewProjectType;
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
	@Inject private PermissionsManager permissionsManager;
	@Inject	private NodeAggregator aggregator;
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		String query = request.getParameter("projectSearch");
		List<ProjectJson> configuredProjects = filter(configurationManager.getConfiguredProjects(), query);
		
		Comparator<ProjectJson> c = new Comparator<ProjectJson>() {
			@Override
			public int compare(ProjectJson o1, ProjectJson o2) {
				return o1.name().compareTo(o2.name());
			}
		};
		Collections.sort(configuredProjects, c);
		
		List<CodeineProject> projects = Lists.newArrayList();
		for (ProjectJson projectJson : configuredProjects) {
			if (permissionsManager.canRead(projectJson.name(), request)){
				VersionItemInfo versionItem = aggregator.aggregate(projectJson.name()).get(Constants.ALL_VERSION);
				projects.add(new CodeineProject(projectJson.name(), versionItem.count()));
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
		} catch (Exception e) {
			throw new IllegalArgumentException();  
		}	
	}
	
	private List<ProjectJson> filter(List<ProjectJson> configuredProjects, final String query) {
		if (null == query){
			return configuredProjects;
		}
		Predicate<ProjectJson> type = new Predicate<ProjectJson>(){
			@Override
			public boolean apply(ProjectJson project){
				return (project.name().toLowerCase().contains(query.toLowerCase()));
			}
		};
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
