package codeine.servlets.front_end;

import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import codeine.api.NodeAggregator;
import codeine.api.VersionItemInfo;
import codeine.configuration.IConfigurationManager;
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
import codeine.servlets.template.ProjectsInView;
import codeine.utils.FilesUtils;
import codeine.utils.TextFileUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;

public class ProjectsListServlet extends AbstractFrontEndServlet
{
	private static final Logger log = Logger.getLogger(ProjectsListServlet.class);
	private static final long serialVersionUID = 1L;
	
	
	@Inject private Links links;
	@Inject	private NodeAggregator aggregator;
	@Inject private IConfigurationManager configurationManager;
	@Inject private PermissionsManager permissionsManager;

	protected ProjectsListServlet() {
		super("projects_list");
	}
	
	@Override
	protected List<String> getJSFiles() {
		return Lists.newArrayList("projects_list","command_executor");
	}
	
	@Override
	protected List<String> getSidebarTemplateFiles() {
		return Lists.newArrayList("command_executor");
	}

	@Override
	protected String getTitle(HttpServletRequest request) {
		return "Codeine Projects";
	}
	@Override
	protected TemplateData doGet(HttpServletRequest request, PrintWriter writer) {
		List<ProjectsInView> views = Lists.newArrayList();
		List<ProjectTemplateLink> projects = getSortedProjects(request);
		String file = Constants.getViewConfPath();
		if (FilesUtils.exists(file)) {
			views = prepareViews(request, file);
		}
		return new ProjectListTemplateData(projects, views, permissionsManager.isAdministrator(request));
	}

	private List<ProjectsInView> prepareViews(HttpServletRequest request, String file) {
		List<ProjectsInView> views = Lists.newArrayList();
		@SuppressWarnings("serial")
		Type listType = new TypeToken<ArrayList<ProjectsTab>>() { }.getType();
		List<ProjectsTab> projects_tabs = gson().fromJson(TextFileUtils.getContents(file), listType);
		for (ProjectsTab tab : projects_tabs) {
			List<ProjectTemplateLink> projectsInView  = Lists.newArrayList();
			for (String projectRegexp : tab.exp()) {
				Pattern pattern = Pattern.compile(projectRegexp);
				for (ProjectJson p : configurationManager.getConfiguredProjects()) {
					if (pattern.matcher(p.name()).matches()){
						createProjectTemplateLink(request, projectsInView, p.name());
					}
				}
			}
			if (!projectsInView.isEmpty()) {
				views.add(new ProjectsInView(tab.name(), projectsInView));
			} else {
				log.debug("ignoring empty view " + tab.name());
			}
		}
		return views;
	}

	@Override
	protected List<TemplateLink> generateNavigation(HttpServletRequest request) {
		return Lists.<TemplateLink>newArrayList();
	}

	@Override
	protected List<TemplateLinkWithIcon> generateMenu(HttpServletRequest request) {
		return getMenuProvider().getMainMenu(request);
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
			createProjectTemplateLink(request, $, project.name());
		}
		return $;
	}

	private void createProjectTemplateLink(HttpServletRequest request, List<ProjectTemplateLink> projectsInView, String name) {
		if (permissionsManager.canRead(name, request)){
			VersionItemInfo versionItem = aggregator.aggregate(name).get(Constants.ALL_VERSION);
			projectsInView.add(new ProjectTemplateLink(name, links.getProjectLink(name), versionItem.count()));
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
		return true;
	}
}
