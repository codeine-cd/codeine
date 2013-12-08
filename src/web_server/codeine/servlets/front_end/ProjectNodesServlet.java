package codeine.servlets.front_end;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import codeine.api.NodeGetter;
import codeine.api.NodeWithMonitorsInfo;
import codeine.configuration.IConfigurationManager;
import codeine.configuration.Links;
import codeine.configuration.NodeMonitor;
import codeine.jsons.command.CommandInfo;
import codeine.jsons.peer_status.PeerStatusString;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.servlet.AbstractFrontEndServlet;
import codeine.servlet.MonitorTemplateLink;
import codeine.servlet.NodeTemplate;
import codeine.servlet.PermissionsManager;
import codeine.servlet.TemplateData;
import codeine.servlet.TemplateLink;
import codeine.servlet.TemplateLinkWithIcon;
import codeine.servlets.template.NameAndAlias;
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
		
		List<NodeTemplate> versionNodes = getVersionsNodes(projectName, versionName, project);
		return new ProjectNodesTemplateData(projectName, versionName, readOnly, versionNodes, getCommandsName(project.commands()), getMonitorsName(project.monitors()));
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
	
	private List<NodeTemplate> getVersionsNodes(String projectName, String versionName, ProjectJson project) {
		List<NodeTemplate> versionNodes = Lists.newArrayList();
		List<NodeWithMonitorsInfo> nodes = nodesGetter.getNodes(projectName,versionName);
		for (NodeWithMonitorsInfo nodeInfo : nodes) {
			List<MonitorTemplateLink> failingMonitors = Lists.newArrayList();
			for (String monitor : nodeInfo.failedMonitors()) {
				String l = link(projectName, nodeInfo.peer().host_port(), nodeInfo, monitor);
				failingMonitors.add(new MonitorTemplateLink(monitor,l,nodeInfo.peer().status() == PeerStatusString.On ? "important" : "default"));
			}
			Comparator<TemplateLink> c = new Comparator<TemplateLink>() {
				@Override
				public int compare(TemplateLink o1, TemplateLink o2) {
					return o1.label().compareTo(o2.label());
				}
			};
			Collections.sort(failingMonitors, c);
			versionNodes.add(new NodeTemplate(nodeInfo.alias(), nodeInfo.name(), nodeInfo.peer().host_port(), failingMonitors,nodeInfo.peer().status() == PeerStatusString.On ? "success" : "disc"));
		}
		return versionNodes;
	}

	
	private String link(String projectName, String peerName, NodeWithMonitorsInfo nodeInfo, String monitorName) {
		return links.getMonitorOutputGuiLink(projectName, peerName, nodeInfo.name(), monitorName);
	}
	
	private List<NameAndAlias> getCommandsName(List<CommandInfo> commands) {
		List<NameAndAlias> $ = Lists.newArrayList();
		for (CommandInfo command : commands) {
			$.add(new NameAndAlias(command.name(), command.title()));
		}
		return $;
	}
	
	private List<String> getMonitorsName(List<NodeMonitor> monitors) {
		List<String> $ = Lists.newArrayList();
		for (NodeMonitor monitor : monitors) {
			$.add(monitor.name());
		}
		return $;
	}
	

	

	
}
