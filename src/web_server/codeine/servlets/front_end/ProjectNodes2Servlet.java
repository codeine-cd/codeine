package codeine.servlets.front_end;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import codeine.configuration.IConfigurationManager;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.servlet.AbstractFrontEndServlet;
import codeine.servlet.PermissionsManager;
import codeine.servlet.TemplateData;
import codeine.servlet.TemplateLink;
import codeine.servlet.TemplateLinkWithIcon;
import codeine.servlets.template.ProjectNodesTemplateData;
import codeine.utils.network.HttpUtils;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class ProjectNodes2Servlet extends AbstractFrontEndServlet {
	
	@Inject	private IConfigurationManager configurationManager;
	@Inject	private PermissionsManager permissionsManager;
	
	private static final long serialVersionUID = 1L;
	
	protected ProjectNodes2Servlet() {
		super("project_nodes2");
	}
	
	@Override
	protected String getTitle(HttpServletRequest request) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		return projectName + " Nodes";
	}
	@Override
	protected TemplateData doGet(HttpServletRequest request, PrintWriter writer) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		String versionName = Constants.ALL_VERSION;
		
		boolean readOnly = !permissionsManager.canCommand(projectName, request);
		ProjectJson project = configurationManager.getProjectForName(projectName);
		
		return new ProjectNodesTemplateData(projectName, versionName, readOnly, ProjectsStatusUtils.getCommandsName(project.commands()), ProjectsStatusUtils.getMonitorsName(project.monitors()));
	}

	@Override
	protected List<TemplateLink> generateNavigation(HttpServletRequest request) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		String versionName = Constants.ALL_VERSION;
		return Lists.<TemplateLink>newArrayList(new TemplateLink(projectName, Constants.PROJECT_STATUS_CONTEXT + "?"+Constants.UrlParameters.PROJECT_NAME+"=" + HttpUtils.encode(projectName)),new TemplateLink(versionName, "#"));
	}

	@Override
	protected List<TemplateLinkWithIcon> generateMenu(HttpServletRequest request) {
		return getMenuProvider().getProjectMenu(request);
	}
	
	@Override
	protected List<String> getJsRenderTemplateFiles() {
		return Lists.newArrayList("project_nodes2_by_version", "nodes_tags");
	}

	@Override
	protected List<String> getJSFiles() {
		return Lists.newArrayList("project_nodes2", "command_history", "commands_toolbar");
	}
	
	@Override
	protected List<String> getSidebarTemplateFiles() {
		return Lists.newArrayList("nodes_tags", "command_history");
	}
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return canReadProject(request);
	}
}
