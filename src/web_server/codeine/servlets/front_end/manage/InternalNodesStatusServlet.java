package codeine.servlets.front_end.manage;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import codeine.ConfigurationManagerServer;
import codeine.api.NodeAggregator;
import codeine.api.VersionItemInfo;
import codeine.model.Constants;
import codeine.servlet.AbstractFrontEndServlet;
import codeine.servlet.FrontEndServletException;
import codeine.servlet.PermissionsManager;
import codeine.servlet.TemplateData;
import codeine.servlet.TemplateLink;
import codeine.servlet.TemplateLinkWithIcon;
import codeine.servlets.front_end.ProjectsStatusUtils;
import codeine.servlets.template.NameAndAlias;
import codeine.servlets.template.ProjectStatusTemplateData;
import codeine.version.VersionItemTemplate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

public class InternalNodesStatusServlet extends AbstractFrontEndServlet {
	private static final String CODEINE_STATUS = "Codeine Nodes Status";
	@Inject	private PermissionsManager permissionsManager;
	@Inject	private NodeAggregator aggregator;
	
	protected InternalNodesStatusServlet() {
		super("project_status", "command_executor", "project_status","commands_toolbar");
	}

	@Override
	protected String getTitle(HttpServletRequest request) {
		return CODEINE_STATUS;
	}
	
	private static final long serialVersionUID = 1L;

	@Override
	protected TemplateData doGet(HttpServletRequest request, PrintWriter writer) throws FrontEndServletException {
		Map<String, VersionItemInfo> nodesVersions2 = aggregator.aggregate(Constants.CODEINE_NODES_PROJECT_NAME);
		Map<String, VersionItemTemplate> nodesVersions = Maps.transformValues(nodesVersions2, ProjectsStatusUtils.getVersionItemTemplateFunction());
		VersionItemInfo allVersions = nodesVersions.remove(Constants.ALL_VERSION);
		List<VersionItemTemplate> values = Lists.newArrayList(nodesVersions.values());
		Collections.sort(values, ProjectsStatusUtils.getVersionComparator());
		List<NameAndAlias> cmd = ProjectsStatusUtils.getCommandsName(ConfigurationManagerServer.NODES_INTERNAL_PROJECT.commands());
		return new ProjectStatusTemplateData(Constants.CODEINE_NODES_PROJECT_NAME, values, allVersions.count(), cmd, false, Constants.CODEINE_NODES_CONTEXT);
	}
	
	@Override
	protected List<TemplateLink> generateNavigation(HttpServletRequest request) {
		return Lists.<TemplateLink>newArrayList(new TemplateLink("Management", Constants.CONFIGURE_CONTEXT), new TemplateLink(CODEINE_STATUS, "#"));
	}

	@Override
	protected List<TemplateLinkWithIcon> generateMenu(HttpServletRequest request) {
		return getMenuProvider().getManageMenu(request);
	}
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return permissionsManager.isAdministrator(request);
	}

}
