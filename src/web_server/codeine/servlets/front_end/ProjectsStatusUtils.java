package codeine.servlets.front_end;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import codeine.api.NodeGetter;
import codeine.api.NodeWithMonitorsInfo;
import codeine.api.VersionItemInfo;
import codeine.configuration.Links;
import codeine.configuration.NodeMonitor;
import codeine.jsons.command.CommandInfo;
import codeine.jsons.peer_status.PeerStatusString;
import codeine.jsons.project.ProjectJson;
import codeine.servlet.MonitorTemplateLink;
import codeine.servlet.NodeTemplate;
import codeine.servlet.TemplateLink;
import codeine.servlets.template.NameAndAlias;
import codeine.version.VersionItemTemplate;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class ProjectsStatusUtils {

	public static Comparator<VersionItemInfo> getVersionComparator() {
		return new Comparator<VersionItemInfo>() {
			@Override
			public int compare(VersionItemInfo o1, VersionItemInfo o2) {
				return Integer.compare(o2.count(), o1.count());
			}
		
		};
	}
	
	public static Function<VersionItemInfo, VersionItemTemplate> getVersionItemTemplateFunction() {
		return new Function<VersionItemInfo, VersionItemTemplate>(){
	
			@Override
			public VersionItemTemplate apply(VersionItemInfo input){
				return new VersionItemTemplate(input);
			}
		};
	}
	
	public static List<NameAndAlias> getCommandsName(List<CommandInfo> commands) {
		List<NameAndAlias> $ = Lists.newArrayList();
		for (CommandInfo command : commands) {
			$.add(new NameAndAlias(command.name(), command.title()));
		}
		return $;
	}
	
	public static List<String> getMonitorsName(List<NodeMonitor> monitors) {
		List<String> $ = Lists.newArrayList();
		for (NodeMonitor monitor : monitors) {
			$.add(monitor.name());
		}
		return $;
	}
	
	public static List<NodeTemplate> getVersionsNodes(String projectName, String versionName, ProjectJson project, NodeGetter nodesGetter, Links links) {
		List<NodeTemplate> versionNodes = Lists.newArrayList();
		List<NodeWithMonitorsInfo> nodes = nodesGetter.getNodes(projectName,versionName);
		for (NodeWithMonitorsInfo nodeInfo : nodes) {
			List<MonitorTemplateLink> failingMonitors = Lists.newArrayList();
			for (String monitor : nodeInfo.failedMonitors()) {
				String l = link(projectName, nodeInfo.peer().host_port(), nodeInfo, monitor, links);
				failingMonitors.add(new MonitorTemplateLink(monitor,l,nodeInfo.peer().status() == PeerStatusString.On ? "important" : "default"));
			}
			Comparator<TemplateLink> c = new Comparator<TemplateLink>() {
				@Override
				public int compare(TemplateLink o1, TemplateLink o2) {
					return o1.label().compareTo(o2.label());
				}
			};
			Collections.sort(failingMonitors, c);
			versionNodes.add(new NodeTemplate(nodeInfo.alias(), nodeInfo.name(), nodeInfo.peer().host_port(), failingMonitors,nodeInfo.peer().status() == PeerStatusString.On ? "success" : "disc", nodeInfo.version(), nodeInfo.tags()));
		}
		return versionNodes;
	}
	
	private static String link(String projectName, String peerName, NodeWithMonitorsInfo nodeInfo, String monitorName, Links links) {
		return links.getMonitorOutputGuiLink(projectName, peerName, nodeInfo.name(), monitorName);
	}
}
