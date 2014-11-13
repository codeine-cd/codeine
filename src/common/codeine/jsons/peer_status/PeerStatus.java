package codeine.jsons.peer_status;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.api.MonitorStatusInfo;
import codeine.api.NodeWithMonitorsInfo;
import codeine.configuration.NodeMonitor;
import codeine.configuration.PathHelper;
import codeine.jsons.collectors.CollectorInfo;
import codeine.jsons.info.CodeineRuntimeInfo;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.utils.network.InetUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class PeerStatus {
	private static final Logger log = Logger.getLogger(PeerStatus.class);

	private Map<String, ProjectStatus> project_name_to_status = Maps.newHashMap();

	@Inject
	private CodeineRuntimeInfo codeineRuntimeInfo;

	public PeerStatus() {

	}

	public String updateStatus(ProjectJson project, MonitorStatusInfo monitor, String node, String alias) {
		NodeWithMonitorsInfo nodeInfo = initStatus(project, node, alias);
		MonitorStatusInfo prevMonitorInfo = nodeInfo.monitors().put(monitor.name(), monitor);
		if (null == prevMonitorInfo) {
			return null;
		}
		return prevMonitorInfo.status();
	}

	private NodeWithMonitorsInfo initStatus(ProjectJson project, String nodeName, String alias) {
		ProjectStatus projectStatus = getProjectStatus(project.name());
		return getNodeInfo(project, nodeName, alias, projectStatus);
	}

	public void removeNode(String project, String node) {
		ProjectStatus projectStatus = getProjectStatus(project);
		projectStatus.removeNodeInfo(node);
	}
	
	private NodeWithMonitorsInfo getNodeInfo(ProjectJson project, String nodeName, String alias,
			ProjectStatus projectStatus) {
		NodeWithMonitorsInfo nodeInfo = projectStatus.nodeInfoOrNull(nodeName);
		//it can be null only when first monitor run on node (single thread)
		if (null == nodeInfo) {
			PeerStatusJsonV2 createJson = createJson();
			Map<String, MonitorStatusInfo> monitors = Maps.newHashMap();
			nodeInfo = new NodeWithMonitorsInfo(createJson, nodeName, alias, project.name(), monitors);
			projectStatus.addNodeInfo(nodeInfo);
		}
		return nodeInfo;
	}

	private ProjectStatus getProjectStatus(String project) {
		synchronized (this) {
			ProjectStatus projectStatus = project_name_to_status.get(project);
			if (null == projectStatus) {
				projectStatus = new ProjectStatus(project);
				HashMap<String, ProjectStatus> tempMap = Maps.newHashMap(project_name_to_status);
				tempMap.put(project, projectStatus);
				project_name_to_status = tempMap;
			}
			return projectStatus;
		}
	}
	
	public void removeProject(String project) {
		ProjectStatus status = null;
		synchronized (this) {
			status = project_name_to_status.remove(project);
		}
		log.info("removed status for project " + project);
		log.info("status was " + status);
	}

	public Map<String, ProjectStatus> project_name_to_status() {
		return Collections.unmodifiableMap(project_name_to_status);
	}

	public PeerStatusJsonV2 createJson() {
		return new PeerStatusJsonV2(InetUtils.getLocalHost().getHostName(), codeineRuntimeInfo.port(),
				codeineRuntimeInfo.version(), codeineRuntimeInfo.startTime(), Constants.getInstallDir(),
				PathHelper.getTarFile(), project_name_to_status, InetUtils.getLocalHost().getHostAddress(), System.getProperty("DNS_DOMAIN_NAME"));
	}

	public String updateVersion(ProjectJson project, String node, String alias, String version) {
		NodeWithMonitorsInfo nodeInfo = initStatus(project, node, alias);
		return nodeInfo.version(version);
	}

	public List<String> updateTags(ProjectJson project, String node, String alias, List<String> tagsList) {
		NodeWithMonitorsInfo nodeInfo = initStatus(project, node, alias);
		return nodeInfo.tags(tagsList);
	}

	public boolean removeNonExistMonitors(ProjectJson project, String node, String alias) {
		List<String> monitorsNotToRemove = Lists.newArrayList();
		for (NodeMonitor nodeMonitor : project.monitors()) {
			monitorsNotToRemove.add(nodeMonitor.name());
		}
		List<String> monitorsToRemove = Lists.newArrayList();
		NodeWithMonitorsInfo nodeInfo = initStatus(project, node, alias);
		for (String monitorName : nodeInfo.monitors().keySet()) {
			if (!monitorsNotToRemove.contains(monitorName)) {
				monitorsToRemove.add(monitorName);
			}
		}
		for (String m : monitorsToRemove) {
			log.info("removing not exist monitor " + m);
			nodeInfo.monitors().remove(m);
		}
		return !monitorsToRemove.isEmpty();
	}
	
	public boolean removeNonExistCollectors(ProjectJson project, String node, String alias) {
		List<String> collectorsNotToRemove = Lists.newArrayList();
		for (CollectorInfo collectorInfo : project.collectors()) {
			collectorsNotToRemove.add(collectorInfo.name());
		}
		NodeWithMonitorsInfo nodeInfo = initStatus(project, node, alias);
		List<String> collectorsToRemove = Lists.newArrayList(nodeInfo.collectors().keySet());
		collectorsToRemove.removeAll(collectorsNotToRemove);
		
		for (String c : collectorsToRemove) {
			log.info("removing not exist collector " + c);
			nodeInfo.collectors().remove(c);
		}
		return !collectorsToRemove.isEmpty();
	}

	public NodeWithMonitorsInfo nodeInfo(ProjectJson project, String node, String alias) {
		return initStatus(project, node, alias);
	}

	public List<String> getTags(String project_name, String node_name) {
		NodeWithMonitorsInfo nodeInfoOrNull = project_name_to_status().get(project_name).nodeInfoOrNull(node_name);
		return nodeInfoOrNull.tags();
	}


}
