package codeine.servlets.template;

import java.util.List;
import java.util.Map.Entry;

import codeine.api.MonitorStatusInfo;
import codeine.api.NodeWithMonitorsInfo;
import codeine.configuration.Links;
import codeine.model.Constants;
import codeine.servlet.MonitorTemplateLink;
import codeine.servlet.TemplateData;

import com.google.common.collect.Lists;



@SuppressWarnings("unused")
public class NodeInfoTemplateData extends TemplateData {
	
	private String version = Constants.NO_VERSION;
	private String project_name;
	private String name;
	private String alias;
	private String peer_status;
	private String peer_address;
	private List<NameAndAlias> commands;
	private List<MonitorTemplateLink> monitors;
	private boolean readonly;
	
	public NodeInfoTemplateData(NodeWithMonitorsInfo node, Links linkHelper,List<NameAndAlias> commands, boolean readonly) 
	{
		super();
		this.commands = commands;
		this.readonly = readonly;
		this.project_name = node.projectName();
		this.name = node.name();
		this.alias = node.alias();
		this.peer_status = node.peer().status().name();
		this.peer_address = node.peer_address();
		monitors = Lists.newArrayList();
		version = node.version();
		for (Entry<String, MonitorStatusInfo> monitor : node.monitors().entrySet()) {
			if (monitor.getKey().equals(Constants.VERSION)) {
				//TODO remove after build 1100
				version = monitor.getValue().status();
			} else	{
				monitors.add(new MonitorTemplateLink(monitor.getValue().name(), linkHelper.getMonitorOutputGuiLink(project_name, node.peer_address(), name, monitor.getValue().name()),  monitor.getValue().pass() ? "success" : "error"));		
			}
		}
	}
}
