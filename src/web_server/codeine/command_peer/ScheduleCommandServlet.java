package codeine.command_peer;


import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import codeine.api.NodeDataJson;
import codeine.api.NodeGetter;
import codeine.api.NodeWithMonitorsInfo;
import codeine.configuration.ConfigurationManager;
import codeine.model.Constants;
import codeine.servlet.AbstractFrontEndServlet;
import codeine.servlet.TemplateData;
import codeine.servlet.TemplateLink;
import codeine.servlet.TemplateLinkWithIcon;
import codeine.servlets.template.SechudleCommandTemplateData;
import codeine.servlets.template.VersionNodesJson;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class ScheduleCommandServlet extends AbstractFrontEndServlet
{
	@Inject	private ConfigurationManager configurationManager;
	@Inject private NodeGetter nodesGetter;
	
	private static final long serialVersionUID = 1L;

	private ScheduleInfoPostDataJson data;
	
	protected ScheduleCommandServlet() {
		super("", "schedule_command", "command_history", "schedule_command", "command_history");
	}

	@Override
	protected TemplateData doGet(HttpServletRequest request, PrintWriter writer) {
		return doPost(request, writer);
	}
	
	@Override
	protected List<TemplateLink> generateNavigation(HttpServletRequest request) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		String commandName = data.command();
		return Lists.<TemplateLink> newArrayList(new TemplateLink(projectName, "/aggregate-node?project=" + projectName),new TemplateLink(commandName, "#"));
	}
	
	@Override
	protected List<TemplateLinkWithIcon> generateMenu(HttpServletRequest request) {
		return getMenuProvider().getProjectMenu(request.getParameter(Constants.UrlParameters.PROJECT_NAME));
	}
	public static class ScheduleInfoPostDataJson {
		private String command;
		private String project;
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
	protected TemplateData doPost(HttpServletRequest request, PrintWriter writer) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		data = gson().fromJson(request.getParameter(Constants.UrlParameters.DATA_NAME), ScheduleInfoPostDataJson.class);
		setTitle(projectName);
		if (data.isNodeMode()) { 
			for (VersionNodesJson versionNodesJson : data.nodes()) {
				versionNodesJson.setId();
			}
		} else {
			int total = 0;
			for (String version : data.versions()) {
				VersionNodesJson versionNodes = new VersionNodesJson(version);
				List<NodeWithMonitorsInfo> nodes = nodesGetter.getNodes(projectName, version);
				for (NodeWithMonitorsInfo nodeWithMonitorsInfo : nodes) {
					switch (data.nodes_selector()) {
					case "All Nodes":
						addNode(versionNodes, nodeWithMonitorsInfo);
						break;
					case "Failing Nodes":
						if (!nodeWithMonitorsInfo.status()){
							addNode(versionNodes, nodeWithMonitorsInfo);
						}
						break;
					case "Number of Nodes":
						if (total < data.num_of_nodes){
							total++;
							addNode(versionNodes, nodeWithMonitorsInfo);
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
		return new SechudleCommandTemplateData(projectName, data.command(), data.nodes());
	}

	protected void addNode(VersionNodesJson versionNodes, NodeWithMonitorsInfo nodeWithMonitorsInfo) {
		versionNodes.node().add(new NodeDataJson(nodeWithMonitorsInfo.peerName(), nodeWithMonitorsInfo.name(), nodeWithMonitorsInfo.alias()));
	}
	
}
