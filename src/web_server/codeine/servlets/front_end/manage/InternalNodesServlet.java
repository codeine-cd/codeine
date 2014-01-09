package codeine.servlets.front_end.manage;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import codeine.ConfigurationManagerServer;
import codeine.api.NodeGetter;
import codeine.configuration.Links;
import codeine.model.Constants;
import codeine.servlet.AbstractFrontEndServlet;
import codeine.servlet.FrontEndServletException;
import codeine.servlet.NodeTemplate;
import codeine.servlet.PermissionsManager;
import codeine.servlet.TemplateData;
import codeine.servlet.TemplateLink;
import codeine.servlet.TemplateLinkWithIcon;
import codeine.servlets.front_end.ProjectsStatusUtils;
import codeine.servlets.template.NameAndAlias;
import codeine.servlets.template.ProjectNodesTemplateData;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class InternalNodesServlet extends AbstractFrontEndServlet {
	
	private static final String CODEINE_NODES = "Codeine Nodes";
	private static final long serialVersionUID = 1L;
	@Inject	private PermissionsManager permissionsManager;
	@Inject	private NodeGetter nodesGetter;
	@Inject	private Links links;

	protected InternalNodesServlet() {
		super("project_nodes", "command_executor", "project_nodes","commands_toolbar");
	}

	@Override
	protected String getTitle(HttpServletRequest request) {
		return CODEINE_NODES;
	}
	@Override
	protected TemplateData doGet(HttpServletRequest request, PrintWriter writer) throws FrontEndServletException {
		String projectName = Constants.CODEINE_NODES_PROJECT_NAME;
		String versionName = request.getParameter(Constants.UrlParameters.VERSION_NAME);
		
		List<NodeTemplate> versionNodes = ProjectsStatusUtils.getVersionsNodes(projectName, versionName, ConfigurationManagerServer.NODES_INTERNAL_PROJECT, nodesGetter, links);
		List<NameAndAlias> cmd = ProjectsStatusUtils.getCommandsName(ConfigurationManagerServer.NODES_INTERNAL_PROJECT.commands());
		List<String> mon = ProjectsStatusUtils.getMonitorsName(ConfigurationManagerServer.NODES_INTERNAL_PROJECT.monitors());
		return new ProjectNodesTemplateData(projectName, versionName, false, versionNodes, cmd, mon);
	}
	
	@Override
	protected List<TemplateLink> generateNavigation(HttpServletRequest request) {
		return Lists.<TemplateLink>newArrayList(new TemplateLink("Management", Constants.CONFIGURE_CONTEXT), new TemplateLink(CODEINE_NODES, "#"));	}

	@Override
	protected List<TemplateLinkWithIcon> generateMenu(HttpServletRequest request) {
		return getMenuProvider().getManageMenu(request);
	}
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return permissionsManager.isAdministrator(request);
	}

}
