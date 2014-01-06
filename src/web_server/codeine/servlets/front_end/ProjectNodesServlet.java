package codeine.servlets.front_end;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import codeine.api.NodeGetter;
import codeine.configuration.IConfigurationManager;
import codeine.configuration.Links;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.servlet.AbstractFrontEndServlet;
import codeine.servlet.NodeTemplate;
import codeine.servlet.PermissionsManager;
import codeine.servlet.TemplateData;
import codeine.servlet.TemplateLink;
import codeine.servlet.TemplateLinkWithIcon;
import codeine.servlets.template.ProjectNodesTemplateData;
import codeine.utils.network.HttpUtils;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class ProjectNodesServlet extends AbstractFrontEndServlet {
	
	@Inject	private NodeGetter nodesGetter;
	@Inject	private IConfigurationManager configurationManager;
	@Inject	private PermissionsManager permissionsManager;
	@Inject	private Links links;
	
	private static final long serialVersionUID = 1L;
	
	protected ProjectNodesServlet() {
		super("", "project_nodes", "command_history", "project_nodes", "command_history", "commands_toolbar");
	}
	
	@Override
	protected TemplateData doGet(HttpServletRequest request, PrintWriter writer) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		String versionName = request.getParameter(Constants.UrlParameters.VERSION_NAME);
		setTitle(projectName + " - " + versionName);
		
		boolean readOnly = !permissionsManager.canCommand(projectName, request);
		ProjectJson project = configurationManager.getProjectForName(projectName);
		
		List<NodeTemplate> versionNodes = ProjectsStatusUtils.getVersionsNodes(projectName, versionName, project, nodesGetter, links);
		return new ProjectNodesTemplateData(projectName, versionName, readOnly, versionNodes, ProjectsStatusUtils.getCommandsName(project.commands()), ProjectsStatusUtils.getMonitorsName(project.monitors()));
	}
	
	@Override
	protected List<TemplateLink> generateNavigation(HttpServletRequest request) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		String versionName = request.getParameter(Constants.UrlParameters.VERSION_NAME);
		return Lists.<TemplateLink>newArrayList(new TemplateLink(projectName, Constants.PROJECT_STATUS_CONTEXT + "?"+Constants.UrlParameters.PROJECT_NAME+"=" + HttpUtils.encode(projectName)),new TemplateLink(versionName, "#"));
	}

	@Override
	protected List<TemplateLinkWithIcon> generateMenu(HttpServletRequest request) {
		return getMenuProvider().getProjectMenu(request);
	}
	

	

	

	
}
