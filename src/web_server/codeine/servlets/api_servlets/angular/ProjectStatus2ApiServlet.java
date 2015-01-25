package codeine.servlets.api_servlets.angular;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.api.MonitorStatusInfo;
import codeine.api.NodeGetter;
import codeine.api.NodeInfo;
import codeine.api.NodeWithMonitorsInfo;
import codeine.configuration.ConfigurationReadManagerServer;
import codeine.configuration.IConfigurationManager;
import codeine.jsons.nodes.NodeDiscoveryStrategy;
import codeine.jsons.peer_status.PeersProjectsStatus;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.permissions.UserPermissionsGetter;
import codeine.servlet.AbstractApiServlet;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

public class ProjectStatus2ApiServlet extends AbstractApiServlet {

	private static final Logger log = Logger
			.getLogger(ProjectStatus2ApiServlet.class);
	private static final long serialVersionUID = 1L;
	@Inject	private NodeGetter nodesGetter;
	@Inject	private IConfigurationManager configurationManager;
	@Inject	private UserPermissionsGetter userPermissionsGetter;
	@Inject	private PeersProjectsStatus peersProjectsStatus;
	
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return canReadProject(request);
	}
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		String projectName = getParameter(request, Constants.UrlParameters.PROJECT_NAME);
		List<NodeWithMonitorsInfo> nodes = nodesGetter.getNodes(projectName,Constants.ALL_VERSION);
		ProjectJson projectJson = configurationManager.getProjectForName(projectName);
		ProjectStatusInfo theNewObject = create(projectJson, nodes, request);
		writeResponseGzipJson(theNewObject, request, response);
	}
	//TODO handle duplicate node and and refactor to other class
	private ProjectStatusInfo create(ProjectJson projectJson, List<NodeWithMonitorsInfo> nodes, HttpServletRequest request) {
		Map<String, Integer> tagCount = Maps.newHashMap();
		Map<String, Integer> monitorCount = Maps.newHashMap();
		Map<String, NodesForVersion> nodesByVersion = Maps.newHashMap();
		int totalNumberOfNodesWithAlerts = 0;
		for (NodeWithMonitorsInfo nodeWithMonitorsInfo : nodes) {
			for (String tag : nodeWithMonitorsInfo.tags()) {
				Integer count = tagCount.get(tag);
				if (count == null) {
					count = 0;
				}
				tagCount.put(tag, count + 1);
			}
			for (String monitor : nodeWithMonitorsInfo.failedMonitors()) {
				Integer count = monitorCount.get(monitor);
				if (count == null) {
					count = 0;
				}
				monitorCount.put(monitor, count + 1);
			}
			for (String monitor : nodeWithMonitorsInfo.failed_collectors()) {
				Integer count = monitorCount.get(monitor);
				if (count == null) {
					count = 0;
				}
				monitorCount.put(monitor, count + 1);
			}
			if (!nodeWithMonitorsInfo.status()) {
				totalNumberOfNodesWithAlerts++;
			}
			NodesForVersion nodeStatusInfoList = nodesByVersion.get(nodeWithMonitorsInfo.version());
			if (nodeStatusInfoList == null) {
				nodeStatusInfoList = new NodesForVersion(nodeWithMonitorsInfo.version());
				nodesByVersion.put(nodeWithMonitorsInfo.version(), nodeStatusInfoList);
			}
			boolean can_command = userPermissionsGetter.user(request).canCommand(projectJson.name(), nodeWithMonitorsInfo.alias());
			nodeStatusInfoList.add(new NodeWithMonitorsInfoApi(nodeWithMonitorsInfo, can_command));
		}
		List<NodesForVersion> nodes_for_version = createNodesList(nodesByVersion, nodes, projectJson);
		int totalNumberOfNodes = nodes_for_version.isEmpty() ? 0 : nodes_for_version.get(0).nodes.size();
		calculatePrecent(totalNumberOfNodes, nodes_for_version);
		List<CountInfo> tag_info = createSortedList(tagCount);
		List<CountInfo> monitor_info = createSortedList(monitorCount);
		NodesForVersion[] offlineNodes = createOfflineNodes(projectJson, nodes);
		if (null != offlineNodes[0] && offlineNodes[0].nodes.size() > 0) {
			nodes_for_version.add(0, offlineNodes[0]);
		}
		if (null != offlineNodes[1] && offlineNodes[1].nodes.size() > 0) {
			nodes_for_version.add(0, offlineNodes[1]);
		}
		return new ProjectStatusInfo(nodes_for_version, tag_info, monitor_info, totalNumberOfNodesWithAlerts, isMoreEnabled(projectJson, nodes));
	}

	private boolean isMoreEnabled(ProjectJson projectJson, List<NodeWithMonitorsInfo> nodes) {
		int nodesCountForMore = 100;
		if (projectJson.node_discovery_startegy() ==  NodeDiscoveryStrategy.Configuration && !projectJson.equals(ConfigurationReadManagerServer.NODES_INTERNAL_PROJECT)) {
			return projectJson.nodes_info().size() > nodesCountForMore;
		}
		return nodes.size() > nodesCountForMore;
	}

	private void calculatePrecent(int totalNumberOfNodes, List<NodesForVersion> nodes_for_version) {
		for (NodesForVersion e : nodes_for_version) {
			e.calculatePrecent(totalNumberOfNodes);
		}
	}

	private List<NodesForVersion> createNodesList(Map<String, NodesForVersion> nodesByVersion, List<NodeWithMonitorsInfo> nodes, ProjectJson projectJson) {
		List<NodesForVersion> $ = Lists.newArrayList();
		for (Entry<String, NodesForVersion> e : nodesByVersion.entrySet()) {
			$.add(e.getValue());
		}
		Comparator<NodesForVersion> c = new Comparator<NodesForVersion>() {
			
			@Override
			public int compare(NodesForVersion o1, NodesForVersion o2) {
				return o2.nodes.size() - o1.nodes.size();
			}
		};
		Collections.sort($, c);
		return $;
	}

	private NodesForVersion[] createOfflineNodes(ProjectJson projectJson, List<NodeWithMonitorsInfo> nodes) {
		NodesForVersion[] $ = new NodesForVersion[2];
		if (projectJson.node_discovery_startegy() != NodeDiscoveryStrategy.Configuration) {
			return $;
		}
		NodesForVersion offlineNodes = new NodesForVersion(Constants.OFFLINE_NODES);
		NodesForVersion notReportedNodes = new NodesForVersion(Constants.NOT_REPORTING_NODES);
		for (NodeInfo nodeInfo : projectJson.nodes_info()) {
			if (notOffline(nodeInfo, nodes)) {
				continue;
			}
			Map<String, MonitorStatusInfo> monitors = Maps.newHashMap();
			String peerStatus = getPeerStatus(nodeInfo.name());
			NodeWithMonitorsInfoApi nodeStatusInfo = new NodeWithMonitorsInfoApi(new NodeWithMonitorsInfo(
					nodeInfo.name(), nodeInfo.alias(), projectJson.name(), monitors, peerStatus), false);
			log.info("adding offline node " + nodeStatusInfo);
			if (peerStatus.equals(Constants.OFFLINE_NODES)) {
				offlineNodes.add(nodeStatusInfo);
			} else {
				notReportedNodes.add(nodeStatusInfo);
			}
		}
		$[0] = offlineNodes;
		$[1] = notReportedNodes;
		return $;
	}
	private String getPeerStatus(String nodeName) {
		String nodeHost = nodeName;
		if (nodeHost.contains(":")) {
			nodeHost = nodeHost.substring(0, nodeHost.indexOf(":"));
		}
		log.info("checking nodeHost " + nodeHost);
		for (String peer : peersProjectsStatus.peer_to_projects().keySet()) {
			if (peer.equals(nodeHost)) {
				return Constants.NOT_REPORTING_NODES;
			}
		}
		return Constants.OFFLINE_NODES;
	}

	private boolean notOffline(NodeInfo nodeInfo, List<NodeWithMonitorsInfo> nodes) {
		for (NodeWithMonitorsInfo nodeWithMonitorsInfo : nodes) {
			if (nodeWithMonitorsInfo.name().equals(nodeInfo.name())) {
				return true;
			}
		}
		return false;
	}

	private List<CountInfo> createSortedList(Map<String, Integer> map) {
		List<CountInfo> $ = Lists.newArrayList();
		for (Entry<String, Integer> e : map.entrySet()) {
			$.add(new CountInfo(e.getKey(), e.getValue()));
		}
		Comparator<CountInfo> c = new Comparator<CountInfo>() {
			
			@Override
			public int compare(CountInfo o1, CountInfo o2) {
				return o1.name.compareTo(o2.name);
			}
		};
		Collections.sort($, c);
		return $;
	}

	@SuppressWarnings("unused")
	public static class ProjectStatusInfo {
		private int any_alert_count;
		private List<NodesForVersion> nodes_for_version;
		private List<CountInfo> tag_info;
		private List<CountInfo> monitor_info;
		private boolean more_nodes_enabled;
		public ProjectStatusInfo(List<NodesForVersion> nodes_for_version, List<CountInfo> tag_info,List<CountInfo> monitor_info,int any_alert_count, boolean more_nodes_enabled) {
			super();
			this.nodes_for_version = nodes_for_version;
			this.tag_info = tag_info;
			this.monitor_info = monitor_info;
			this.any_alert_count = any_alert_count;
			this.more_nodes_enabled = more_nodes_enabled;
		}
		
	}
	
	@SuppressWarnings("unused")
	public static class NodesForVersion {
		private String version;
		private List<NodeWithMonitorsInfo> nodes = Lists.newArrayList();
		private int failing_nodes_count;
		private int failing_nodes_precent;
		private int not_failing_nodes_precent;

		public NodesForVersion(String version) {
			this.version = version;
		}

		public void calculatePrecent(int totalNumberOfNodes) {
			if (totalNumberOfNodes == 0) {
				return;
			}
			failing_nodes_precent = failing_nodes_count * 100 / totalNumberOfNodes;
			not_failing_nodes_precent = (nodes.size() - failing_nodes_count)  * 100 / totalNumberOfNodes;
		}

		public void add(NodeWithMonitorsInfo nodeStatusInfo) {
			nodes.add(nodeStatusInfo);
			if (!nodeStatusInfo.status()) {
				failing_nodes_count++;
			}
		}
	}
	@SuppressWarnings("unused")
	public static class CountInfo {
		private String name;
		private int count;
		public CountInfo(String name, int count) {
			super();
			this.name = name;
			this.count = count;
		}
		
	}

}
