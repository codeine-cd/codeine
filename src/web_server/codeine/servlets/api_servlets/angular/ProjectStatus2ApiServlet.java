package codeine.servlets.api_servlets.angular;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeine.api.NodeGetter;
import codeine.api.NodeWithMonitorsInfo;
import codeine.configuration.IConfigurationManager;
import codeine.jsons.peer_status.PeerStatusString;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.servlet.AbstractServlet;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

public class ProjectStatus2ApiServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	@Inject	private NodeGetter nodesGetter;
	@Inject	private IConfigurationManager configurationManager;
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return canReadProject(request);
	}
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		List<NodeWithMonitorsInfo> nodes = nodesGetter.getNodes(projectName,Constants.ALL_VERSION);
		ProjectJson projectJson = configurationManager.getProjectForName(projectName);
		ProjectStatusInfo theNewObject = create(projectJson, nodes);
		writeResponseJson(response, theNewObject);
	}
	
	private ProjectStatusInfo create(ProjectJson projectJson, List<NodeWithMonitorsInfo> nodes) {
		int totalNumberOfNodes = nodes.size();
		Map<String, Integer> tagCount = Maps.newHashMap();
		Map<String, Integer> monitorCount = Maps.newHashMap();
		Map<String, NodesForVersion> nodesByVersion = Maps.newHashMap();
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
			NodesForVersion nodeStatusInfoList = nodesByVersion.get(nodeWithMonitorsInfo.version());
			if (nodeStatusInfoList == null) {
				nodeStatusInfoList = new NodesForVersion(nodeWithMonitorsInfo.version());
				nodesByVersion.put(nodeWithMonitorsInfo.version(), nodeStatusInfoList);
			}
			nodeStatusInfoList.add(new NodeStatusInfo(nodeWithMonitorsInfo));
		}
		calculatePrecent(totalNumberOfNodes, nodesByVersion);
		List<NodesForVersion> nodes_for_version = createNodesList(nodesByVersion);
		List<CountInfo> tag_info = createSortedList(tagCount);
		List<CountInfo> monitor_info = createSortedList(monitorCount);
		return new ProjectStatusInfo(nodes_for_version, tag_info, monitor_info);
	}

	private void calculatePrecent(int totalNumberOfNodes, Map<String, NodesForVersion> nodesByVersion) {
		for (Entry<String, NodesForVersion> e : nodesByVersion.entrySet()) {
			e.getValue().calculatePrecent(totalNumberOfNodes);
		}
	}

	private List<NodesForVersion> createNodesList(Map<String, NodesForVersion> nodesByVersion) {
		List<NodesForVersion> $ = Lists.newArrayList();
		for (Entry<String, NodesForVersion> e : nodesByVersion.entrySet()) {
			$.add(e.getValue());
		}
		Comparator<NodesForVersion> c = new Comparator<NodesForVersion>() {
			
			@Override
			public int compare(NodesForVersion o1, NodesForVersion o2) {
				return o1.nodes.size() - o2.nodes.size();
			}
		};
		Collections.sort($, c);
		return $;
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
		private List<NodesForVersion> nodes_for_version;
		private List<CountInfo> tag_info;
		private List<CountInfo> monitor_info;
		public ProjectStatusInfo(List<NodesForVersion> nodes_for_version, List<CountInfo> tag_info,
				List<CountInfo> monitor_info) {
			super();
			this.nodes_for_version = nodes_for_version;
			this.tag_info = tag_info;
			this.monitor_info = monitor_info;
		}
		
	}

	@SuppressWarnings("unused")
	public static class NodesForVersion {
		private String version;
		private List<NodeStatusInfo> nodes = Lists.newArrayList();
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
			failing_nodes_precent = failing_nodes_count / totalNumberOfNodes * 100;
			not_failing_nodes_precent = (nodes.size() - failing_nodes_count) / totalNumberOfNodes * 100;
		}

		public void add(NodeStatusInfo nodeStatusInfo) {
			nodes.add(nodeStatusInfo);
			if (!nodeStatusInfo.failed_monitors.isEmpty()) {
				failing_nodes_count++;
			}
		}
	}
	@SuppressWarnings("unused")
	public static class NodeStatusInfo {
		private String node_alias;
		private String node_name;
		private List<String> failed_monitors;
		private List<String> tags;
		private String peer_key;
		private PeerStatusString peer_status;

		public NodeStatusInfo(NodeWithMonitorsInfo nodeWithMonitorsInfo) {
			super();
			this.node_alias = nodeWithMonitorsInfo.node_alias();
			this.node_name = nodeWithMonitorsInfo.name();
			this.failed_monitors = nodeWithMonitorsInfo.failed_monitors();
			this.tags = nodeWithMonitorsInfo.tags();
			this.peer_key = nodeWithMonitorsInfo.peer_key();
			this.peer_status = nodeWithMonitorsInfo.peer().status();
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
