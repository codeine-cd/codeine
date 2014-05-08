package codeine.jsons.peer_status;

import java.util.Collection;
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
import codeine.jsons.info.CodeineRuntimeInfo;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.utils.network.InetUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class PeerStatus {
	private static final Logger log = Logger.getLogger(PeerStatus.class);

	private Map<String, ProjectStatus> project_name_to_status = Maps.newHashMap();//Lists.newArrayList();
	
	@Inject private CodeineRuntimeInfo codeineRuntimeInfo;
	
	public PeerStatus() {
		
	}
	
	public String updateStatus(ProjectJson project, MonitorStatusInfo monitor, String node, String alias) {
		NodeWithMonitorsInfo nodeInfo = initStatus(project, node, alias);
		MonitorStatusInfo prevMonitorInfo = nodeInfo.monitors().put(monitor.name(), monitor);
		if (null == prevMonitorInfo){
			return null;
		}
		return prevMonitorInfo.status();
	}

	private synchronized NodeWithMonitorsInfo initStatus(ProjectJson project, String nodeName, String alias) {
		ProjectStatus projectStatus = project_name_to_status().get(project.name());
		if (null == projectStatus){
			List<NodeWithMonitorsInfo> nodesInfo = Lists.newArrayList();
			projectStatus = new ProjectStatus(project.name(), nodesInfo);
			addProjectStatus(project.name(), projectStatus);
		}
		NodeWithMonitorsInfo nodeInfo = projectStatus.nodeInfoOrNull(nodeName);
		if (null == nodeInfo){
			PeerStatusJsonV2 createJson = createJson();
			Map<String, MonitorStatusInfo> monitors = Maps.newHashMap();
			nodeInfo = new NodeWithMonitorsInfo(createJson, nodeName, alias, project.name(), monitors);
			projectStatus.addNodeInfo(nodeInfo);
		}
		return nodeInfo;
	}
	
	private void addProjectStatus(String name, ProjectStatus status) {
		HashMap<String, ProjectStatus> tempList = Maps.newHashMap(project_name_to_status);
		tempList.put(name, status);
		project_name_to_status = tempList;
	}
	
	
	public Map<String, ProjectStatus> project_name_to_status() {
		return Collections.unmodifiableMap(project_name_to_status);
	}
	
	public PeerStatusJsonV2 createJson() {
		return new PeerStatusJsonV2(InetUtils.getLocalHost().getHostName(), codeineRuntimeInfo.port(), codeineRuntimeInfo.version(), codeineRuntimeInfo.startTime(), Constants.getInstallDir(), PathHelper.getTarFile(),project_name_to_status, InetUtils.getLocalHost().getHostAddress());
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

	public NodeWithMonitorsInfo nodeInfo(ProjectJson project, String node, String alias) {
		return initStatus(project, node, alias);
	}

	public Collection<?> getTags(String project_name, String node_name) {
		return project_name_to_status().get(project_name).nodeInfoOrNull(node_name).tags();
	}

}
