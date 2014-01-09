package codeine.servlets.front_end;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import codeine.api.NodeAggregator;
import codeine.api.VersionItemInfo;
import codeine.configuration.IConfigurationManager;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.servlet.AbstractFrontEndServlet;
import codeine.servlet.PermissionsManager;
import codeine.servlet.TemplateData;
import codeine.servlet.TemplateLink;
import codeine.servlet.TemplateLinkWithIcon;
import codeine.servlets.template.ProjectStatusTemplateData;
import codeine.utils.UrlUtils;
import codeine.version.VersionItemTemplate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

public class ProjectStatusServlet extends AbstractFrontEndServlet {
	
	@Inject	private IConfigurationManager configurationManager;
	@Inject	private NodeAggregator aggregator;
	@Inject	private PermissionsManager permissionsManager;
	
	
	private static final long serialVersionUID = 1L;
	
	protected ProjectStatusServlet() {
		super("project_status", "command_history", "command_history", "project_status", "commands_toolbar");
	}
	
	@Override
	protected String getTitle(HttpServletRequest request) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		return projectName;
	}
	
	@Override
	protected TemplateData doGet(HttpServletRequest request, PrintWriter writer) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		Map<String, VersionItemInfo> nodesVersions2 = aggregator.aggregate(projectName);
		Map<String, VersionItemTemplate> nodesVersions = Maps.transformValues(nodesVersions2, ProjectsStatusUtils.getVersionItemTemplateFunction());
		VersionItemInfo allVersions = nodesVersions.remove(Constants.ALL_VERSION);
		List<VersionItemTemplate> values = Lists.newArrayList(nodesVersions.values());
		Collections.sort(values, ProjectsStatusUtils.getVersionComparator());
		
		boolean readOnly = !permissionsManager.canCommand(projectName, request);
		ProjectJson project = configurationManager.getProjectForName(projectName);
		
		return new ProjectStatusTemplateData(projectName, values, allVersions.count(), ProjectsStatusUtils.getCommandsName(project.commands()), readOnly, Constants.PROJECT_NODES_CONTEXT);
	}
	@Override
	protected List<TemplateLink> generateNavigation(HttpServletRequest request) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		return Lists.<TemplateLink>newArrayList(new TemplateLink(projectName, UrlUtils.buildUrl(Constants.PROJECT_STATUS_CONTEXT, ImmutableMap.of(Constants.UrlParameters.PROJECT_NAME, projectName))));
	}
	@Override
	protected List<TemplateLinkWithIcon> generateMenu(HttpServletRequest request) {
		return getMenuProvider().getProjectMenu(request);
	}
}
