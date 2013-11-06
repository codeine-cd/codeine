package codeine.servlets;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import codeine.api.NodeGetter;
import codeine.api.NodeWithMonitorsInfo;
import codeine.configuration.ConfigurationManager;
import codeine.configuration.HttpCollector;
import codeine.configuration.Links;
import codeine.jsons.command.CommandJson;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.servlet.AbstractFrontEndServlet;
import codeine.servlet.NodeTemplate;
import codeine.servlet.PermissionsManager;
import codeine.servlet.TemplateData;
import codeine.servlet.TemplateLink;
import codeine.servlet.TemplateLinkWithIcon;
import codeine.servlets.template.NameAndAlias;
import codeine.servlets.template.ProjectNodesTemplateData;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class DashboardServletV2 extends AbstractFrontEndServlet {
	
	@Inject	private NodeGetter nodesGetter;
	@Inject	private ConfigurationManager configurationManager;
	@Inject	private PermissionsManager permissionsManager;
	@Inject	private Links links;
	
	private static final long serialVersionUID = 1L;
	
	protected DashboardServletV2() {
		super("", "project_nodes", "command_history", "project_nodes", "command_history");
	}
	
	@Override
	protected TemplateData doGet(HttpServletRequest request, PrintWriter writer) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		String versionName = request.getParameter(Constants.UrlParameters.VERSION_NAME);
		setTitle(projectName + " - " + versionName);
		
		boolean readOnly = !permissionsManager.isModifiable(projectName, request);
		ProjectJson project = configurationManager.getProjectForName(projectName);
		
		List<NodeTemplate> versionNodes = getVersionsNodes(projectName, versionName, project);
		return new ProjectNodesTemplateData(projectName, versionName, readOnly, versionNodes, getCommandsName(project.commands()), getMonitorsName(project.collectors()));
	}
	
	@Override
	protected List<TemplateLink> generateNavigation(HttpServletRequest request) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		String versionName = request.getParameter(Constants.UrlParameters.VERSION_NAME);
		return Lists.<TemplateLink>newArrayList(new TemplateLink(projectName, "/aggregate-node?project=" + projectName),new TemplateLink(versionName, "#"));
	}

	@Override
	protected List<TemplateLinkWithIcon> generateMenu(HttpServletRequest request) {
		return getMenuProvider().getProjectMenu(request.getParameter(Constants.UrlParameters.PROJECT_NAME));
	}
	
	private List<NodeTemplate> getVersionsNodes(String projectName, String versionName, ProjectJson project) {
		List<NodeTemplate> versionNodes = Lists.newArrayList();
			List<NodeWithMonitorsInfo> nodes = nodesGetter.getNodes(projectName,versionName);
			for (NodeWithMonitorsInfo nodeInfo : nodes) {
				List<TemplateLink> failingMonitors = Lists.newArrayList();
				for (String monitor : nodeInfo.failedMonitors()) {
					String l = link(projectName, nodeInfo.peerName(), nodeInfo, monitor);
					failingMonitors.add(new TemplateLink(monitor,l));
				}
				Comparator<TemplateLink> c = new Comparator<TemplateLink>() {
					@Override
					public int compare(TemplateLink o1, TemplateLink o2) {
						return o1.label().compareTo(o2.label());
					}
				};
				Collections.sort(failingMonitors, c);
				versionNodes.add(new NodeTemplate(nodeInfo.alias(), nodeInfo.name(), nodeInfo.peerName(), failingMonitors));
			}
		return versionNodes;
	}

	
	private String link(String projectName, String peerName, NodeWithMonitorsInfo nodeInfo, String monitorName) {
		return links.getMonitorOutputGuiLink(projectName, peerName, nodeInfo.name(), monitorName);
	}
	
	private List<NameAndAlias> getCommandsName(List<CommandJson> commands) {
		List<NameAndAlias> $ = Lists.newArrayList();
		for (CommandJson command : commands) {
			$.add(new NameAndAlias(command.name(), command.title()));
		}
		return $;
	}
	
	private List<String> getMonitorsName(List<HttpCollector> monitors) {
		List<String> $ = Lists.newArrayList();
		for (HttpCollector monitor : monitors) {
			$.add(monitor.name());
		}
		return $;
	}
	

	

	
}
