package codeine.servlets;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import codeine.configuration.ConfigurationManager;
import codeine.configuration.Links;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.servlet.AbstractFrontEndServlet;
import codeine.servlet.PermissionsManager;
import codeine.servlet.ProjectTemplateLink;
import codeine.servlet.TemplateData;
import codeine.servlet.TemplateLink;
import codeine.servlet.TemplateLinkWithIcon;
import codeine.servlets.template.ProjectListTemplateData;
import codeine.version.VersionItem;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class ProjectsDashboardServlet extends AbstractFrontEndServlet
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(ProjectsDashboardServlet.class);
	private static final long serialVersionUID = 1L;
	
	protected ProjectsDashboardServlet() {
		super("Codeine", "projects_list","command_executor", "projects");
	}
	
	@Inject private Links links;
	@Inject	private NodeAggregator aggregator;
	@Inject private ConfigurationManager configurationManager;
	@Inject private PermissionsManager permissionsManager;

	@Override
	protected TemplateData doGet(HttpServletRequest request, PrintWriter writer) {
		List<ProjectTemplateLink> projects = getSortedProjects(request);
		return new ProjectListTemplateData(projects);
	}

	@Override
	protected List<TemplateLink> generateNavigation(HttpServletRequest request) {
		return Lists.<TemplateLink>newArrayList();
	}

	@Override
	protected List<TemplateLinkWithIcon> generateMenu(HttpServletRequest request) {
		return getMenuProvider().getMainMenu();
	}
	
	private List<ProjectTemplateLink> getSortedProjects(HttpServletRequest request) {
		String query = request.getParameter("projectSearch");
		List<ProjectJson> configuredProjects = filter(configurationManager.getConfiguredProjects(), query);
		Comparator<ProjectJson> c = new Comparator<ProjectJson>() {
			@Override
			public int compare(ProjectJson o1, ProjectJson o2) {
				return o1.name().compareTo(o2.name());
			}
		};
		Collections.sort(configuredProjects, c);
		List<ProjectTemplateLink> $ = Lists.newArrayList();
		for (ProjectJson project : configuredProjects) {
			if (permissionsManager.isModifiable(project.name(), request)){
				VersionItem versionItem = aggregator.aggregate(project).get(Constants.ALL_VERSION);
				$.add(new ProjectTemplateLink(project.name(), links.getProjectLink(project), versionItem.count()));
			}
		}
		return $;
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
}
