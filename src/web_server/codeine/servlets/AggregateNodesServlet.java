package codeine.servlets;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import codeine.configuration.ConfigurationManager;
import codeine.jsons.command.CommandJson;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.servlet.AbstractFrontEndServlet;
import codeine.servlet.PermissionsManager;
import codeine.servlet.TemplateData;
import codeine.servlet.TemplateLink;
import codeine.servlet.TemplateLinkWithIcon;
import codeine.servlets.template.NameAndAlias;
import codeine.servlets.template.ProjectStatusTemplateData;
import codeine.utils.UrlUtils;
import codeine.version.VersionItem;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class AggregateNodesServlet extends AbstractFrontEndServlet {
	
	@Inject	private ConfigurationManager configurationManager;
	@Inject	private NodeAggregator aggregator;
	@Inject	private PermissionsManager permissionsManager;
	
	private static final long serialVersionUID = 1L;
	
	protected AggregateNodesServlet() {
		super("", "project_status", "command_history", "command_history", "project_status");
	}
	
	@Override
	protected TemplateData doGet(HttpServletRequest request, PrintWriter writer) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		Comparator<VersionItem> comparator = new Comparator<VersionItem>() {
			@Override
			public int compare(VersionItem o1, VersionItem o2) {
				return Integer.compare(o2.count(), o1.count());
			}
		};
		
		Map<String, VersionItem> nodesVersions = aggregator.aggregate(configurationManager.getProjectForName(projectName));
		VersionItem allVersions = nodesVersions.remove(Constants.ALL_VERSION);
		List<VersionItem> values = Lists.newArrayList(nodesVersions.values());
		Collections.sort(values, comparator);
		setTitle(projectName);
		
		boolean readOnly = !permissionsManager.isModifiable(projectName, request);
		ProjectJson project = configurationManager.getProjectForName(projectName);
		
		return new ProjectStatusTemplateData(projectName, values, allVersions.count(), getCommandsName(project.commands()), readOnly);
	}
	@Override
	protected List<TemplateLink> generateNavigation(HttpServletRequest request) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		return Lists.<TemplateLink>newArrayList(new TemplateLink(projectName, UrlUtils.buildUrl(Constants.AGGREGATE_NODE_CONTEXT, ImmutableMap.of(Constants.UrlParameters.PROJECT_NAME, projectName))));
	}
	@Override
	protected List<TemplateLinkWithIcon> generateMenu(HttpServletRequest request) {
		return getMenuProvider().getProjectMenu(request.getParameter(Constants.UrlParameters.PROJECT_NAME));
	}
	
	private List<NameAndAlias> getCommandsName(List<CommandJson> commands) {
		List<NameAndAlias> $ = Lists.newArrayList();
		for (CommandJson command : commands) {
			$.add(new NameAndAlias(command.name(), command.title()));
		}
		return $;
	}
}
