package codeine.servlets.front_end;


import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import codeine.api.NodeGetter;
import codeine.api.NodeWithMonitorsInfo;
import codeine.configuration.IConfigurationManager;
import codeine.jsons.command.CommandInfo;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.servlet.AbstractFrontEndServlet;
import codeine.servlet.TemplateData;
import codeine.servlet.TemplateLink;
import codeine.servlet.TemplateLinkWithIcon;
import codeine.servlets.template.SechudleCommandTemplateData;
import codeine.servlets.template.VersionNodesJson;
import codeine.utils.network.HttpUtils;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class ScheduleCommandServlet extends AbstractFrontEndServlet
{
	private static final String DATA_PARSED = "data_parsed";
	@Inject	private IConfigurationManager configurationManager;
	@Inject private NodeGetter nodesGetter;
	
	private static final long serialVersionUID = 1L;

	protected ScheduleCommandServlet() {
		super("schedule_command");
	}
	
	@Override
	protected List<String> getJSFiles() {
		return Lists.newArrayList("schedule_command", "command_history");
	}
	
	@Override
	protected List<TemplateLink> generateNavigation(HttpServletRequest request) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		ScheduleInfoPostDataJson data = (ScheduleInfoPostDataJson) request.getAttribute(DATA_PARSED);
		String commandName = data.command();
		return Lists.<TemplateLink> newArrayList(new TemplateLink(projectName, Constants.PROJECT_STATUS_CONTEXT + "?project=" + HttpUtils.encodeURL(projectName)),new TemplateLink(commandName, "#"));
	}
	
	@Override
	protected List<TemplateLinkWithIcon> generateMenu(HttpServletRequest request) {
		return getMenuProvider().getProjectMenu(request);
	}
	
	@Override
	protected String getTitle(HttpServletRequest request) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		return projectName + " Schedule Command";
	}
	@Override
	protected TemplateData doPost(HttpServletRequest request, PrintWriter writer) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		ScheduleInfoPostDataJson data = gson().fromJson(request.getParameter(Constants.UrlParameters.DATA_NAME), ScheduleInfoPostDataJson.class);
		request.setAttribute(DATA_PARSED, data);
		if (data.isNodeMode()) { 
			for (VersionNodesJson versionNodesJson : data.nodes()) {
				versionNodesJson.setId();
			}
		} else {
			updateNodesFromVersions(projectName, data);
		}

		ProjectJson project = configurationManager.getProjectForName(projectName);
		CommandInfo command = project.commandForName(data.command());
		return new SechudleCommandTemplateData(projectName, command, data.nodes());
	}

	public void updateNodesFromVersions(String projectName, ScheduleInfoPostDataJson data) {
		int total = 0;
		for (String version : data.versions()) {
			VersionNodesJson versionNodes = new VersionNodesJson(version);
			List<NodeWithMonitorsInfo> nodes = nodesGetter.getNodes(projectName, version);
			for (NodeWithMonitorsInfo nodeWithMonitorsInfo : nodes) {
				switch (data.nodes_selector()) {
				case "All Selected":
					versionNodes.node().add(nodeWithMonitorsInfo);
					break;
				case "Failing Nodes":
					if (!nodeWithMonitorsInfo.status()){
						versionNodes.node().add(nodeWithMonitorsInfo);
					}
					break;
				case "Number of Nodes":
					if (total < data.num_of_nodes){
						total++;
						versionNodes.node().add(nodeWithMonitorsInfo);
					}
					break;
				default:
					throw new UnsupportedOperationException(data.nodes_selector() + " is not a valid selector");
				}
				
			}
			versionNodes.updateCount();
			data.nodes().add(versionNodes);
		}
	}
	
	public static class ScheduleInfoPostDataJson {
		private String command;
		private List<String> versions;
		private List<VersionNodesJson> nodes = Lists.newArrayList();
		private String nodes_selector;
		private Integer num_of_nodes;
		
		public boolean isNodeMode(){
			return (null != nodes && ! nodes.isEmpty());
		}

		public List<VersionNodesJson> nodes() {
			return nodes;
		}

		public String command() {
			return command;
		}

		public List<String> versions() {
			return versions;
		}

		public String nodes_selector() {
			return nodes_selector;
		}
	}
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return canCommandProject(request);
	}
}
