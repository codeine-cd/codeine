package codeine.servlets.api_servlets;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.configuration.IConfigurationManager;
import codeine.jsons.project.ProjectJson;
import codeine.servlet.AbstractServlet;
import codeine.servlet.PermissionsManager;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class ProjectsListApiServlet extends AbstractServlet
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(ProjectsListApiServlet.class);
	private static final long serialVersionUID = 1L;
	@Inject private IConfigurationManager configurationManager;
	@Inject private PermissionsManager permissionsManager;
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		String query = request.getParameter("projectSearch");
		List<ProjectJson> configuredProjects = filter(configurationManager.getConfiguredProjects(), query);
		List<ProjectJson> projects = Lists.newArrayList();
		for (ProjectJson projectJson : configuredProjects) {
			if (permissionsManager.canRead(projectJson.name(), request)){
				projects.add(projectJson);
			}
		}
		writeResponseJson(response, projects);
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
		return true;
	}
}
