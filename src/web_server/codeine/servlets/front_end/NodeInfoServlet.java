package codeine.servlets.front_end;

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
import codeine.servlets.template.NodeInfoTemplateData;
import codeine.utils.network.HttpUtils;

import com.google.common.collect.Lists;


public class NodeInfoServlet extends AbstractFrontEndServlet {

	private static final long serialVersionUID = 1L;
	@Inject private NodeGetter nodesGetter;
	@Inject private Links links;
	@Inject private IConfigurationManager configurationManager;
	@Inject private PermissionsManager permissionsManager;
	
	protected NodeInfoServlet() {
		super("node_info");
	}

	@Override
	protected List<String> getJSFiles() {
		return Lists.newArrayList("node_info", "command_history");
	}
	
	@Override
	protected String getTitle(HttpServletRequest request) {
		return request.getParameter(Constants.UrlParameters.NODE_NAME) + " Node Info";
	}
	@Override
	protected TemplateData doGet(HttpServletRequest request, PrintWriter writer) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		String nodeName = request.getParameter(Constants.UrlParameters.NODE_NAME);
		
		ProjectJson project = configurationManager.getProjectForName(projectName);
		boolean readOnly = !permissionsManager.canCommand(projectName, request);
		
		NodeWithMonitorsInfo node = nodesGetter.getNodeByName(projectName, nodeName);
		return new NodeInfoTemplateData(node,links, ProjectsStatusUtils.getCommandsName(project.commands()),readOnly);
	}
	
	@Override
	protected List<TemplateLink> generateNavigation(HttpServletRequest request) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		String nodeName = request.getParameter(Constants.UrlParameters.NODE_NAME);
		NodeWithMonitorsInfo node = nodesGetter.getNodeByName(projectName, nodeName);
		return Lists.<TemplateLink>newArrayList(new TemplateLink(projectName, Constants.PROJECT_STATUS_CONTEXT + "?"+Constants.UrlParameters.PROJECT_NAME+"=" + HttpUtils.encode(projectName)),new TemplateLink(node.alias(), "#"));
	}

	@Override
	protected List<TemplateLinkWithIcon> generateMenu(HttpServletRequest request) {
		return getMenuProvider().getProjectMenu(request);
	}
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return canReadProject(request);
	}

}
