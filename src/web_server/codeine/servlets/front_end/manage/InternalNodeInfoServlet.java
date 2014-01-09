package codeine.servlets.front_end.manage;

import java.io.PrintWriter;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import codeine.api.NodeGetter;
import codeine.api.NodeWithMonitorsInfo;
import codeine.configuration.IConfigurationManager;
import codeine.configuration.Links;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.servlet.AbstractFrontEndServlet;
import codeine.servlet.PermissionsManager;
import codeine.servlet.TemplateData;
import codeine.servlet.TemplateLink;
import codeine.servlet.TemplateLinkWithIcon;
import codeine.servlets.front_end.ProjectsStatusUtils;
import codeine.servlets.template.NodeInfoTemplateData;

import com.google.common.collect.Lists;


public class InternalNodeInfoServlet extends AbstractFrontEndServlet {

	private static final long serialVersionUID = 1L;
	@Inject private NodeGetter nodesGetter;
	@Inject private Links links;
	@Inject private IConfigurationManager configurationManager;
	@Inject private PermissionsManager permissionsManager;
	
	protected InternalNodeInfoServlet() {
		super("node_info", "command_executor", "node_info", "commands_toolbar");
	}

	@Override
	protected String getTitle(HttpServletRequest request) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		return "Configure " + projectName;
	}
	
	@Override
	protected TemplateData doGet(HttpServletRequest request, PrintWriter writer) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		String nodeName = request.getParameter(Constants.UrlParameters.NODE_NAME);
		
		ProjectJson project = configurationManager.getProjectForName(projectName);
		boolean readOnly = false;
		
		NodeWithMonitorsInfo node = nodesGetter.getNodeByName(projectName, nodeName);
		return new NodeInfoTemplateData(node,links, ProjectsStatusUtils.getCommandsName(project.commands()),readOnly);
	}
	
	@Override
	protected List<TemplateLink> generateNavigation(HttpServletRequest request) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		String nodeName = request.getParameter(Constants.UrlParameters.NODE_NAME);
		NodeWithMonitorsInfo node = nodesGetter.getNodeByName(projectName, nodeName);
		return Lists.<TemplateLink>newArrayList(new TemplateLink("Management", Constants.CONFIGURE_CONTEXT), new TemplateLink(node.alias(), "#"));
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
