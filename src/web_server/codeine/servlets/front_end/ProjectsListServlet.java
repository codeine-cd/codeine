package codeine.servlets.front_end;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

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
import com.google.inject.Inject;

public class ProjectsListServlet extends AbstractFrontEndServlet
{
	private static final Logger log = Logger.getLogger(ProjectsListServlet.class);
	private static final long serialVersionUID = 1L;
	
	protected ProjectsListServlet() {
		super("Codeine", "projects_list","command_executor", "projects_list","command_executor");
	}
	
	@Inject private Links links;
	@Inject	private NodeAggregator aggregator;
	@Inject private IConfigurationManager configurationManager;
	@Inject private PermissionsManager permissionsManager;

	@Override
	protected TemplateData doGet(HttpServletRequest request, PrintWriter writer) {
		List<ProjectsInView> lst = Lists.newArrayList();
		List<ProjectTemplateLink> projects = getSortedProjects(request);
		String file = Constants.getWebConfPath();
		if (FilesUtils.exists(file)) {
			CodeineWebConf conf = gson().fromJson(TextFileUtils.getContents(file), CodeineWebConf.class);
			for (Entry<String, List<String>> e : conf.views().entrySet()) {
				List<ProjectTemplateLink> projectsInView  = Lists.newArrayList();
				for (String project : e.getValue()) {
					if (configurationManager.hasProject(project)) {
						createProjectTemplateLink(request, projectsInView, project);
					}
					else {
						log.warn("project not configured: " + project);
					}
				}
				if (!projectsInView.isEmpty()) {
					lst.add(new ProjectsInView(e.getKey(), projectsInView));
				}
			}
		}
		return new ProjectListTemplateData(projects, lst, permissionsManager.isAdministrator(request));
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

	private void createProjectTemplateLink(HttpServletRequest request, List<ProjectTemplateLink> $, String name) {
		if (permissionsManager.canRead(name, request)){
			VersionItemInfo versionItem = aggregator.aggregate(name).get(Constants.ALL_VERSION);
			$.add(new ProjectTemplateLink(name, links.getProjectLink(name), versionItem.count()));
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
}
