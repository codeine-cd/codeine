package codeine.jsons.peer_status;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import codeine.api.MonitorInfo;
import codeine.api.NodeWithMonitorsInfo;
import codeine.configuration.PathHelper;
import codeine.jsons.info.CodeineRuntimeInfo;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.utils.network.InetUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class PeerStatus {
	private Map<String, ProjectStatus> project_name_to_status = Maps.newHashMap();//Lists.newArrayList();
	
	@Inject private CodeineRuntimeInfo codeineRuntimeInfo;
	
	public PeerStatus() {
		
	}
	
	public String updateStatus(ProjectJson project, MonitorInfo monitor, String node, String alias) {
		NodeWithMonitorsInfo nodeInfo = initStatus(project, node, alias);
		MonitorInfo prevMonitorInfo = nodeInfo.monitors().put(monitor.name(), monitor);
		if (null == prevMonitorInfo){
			return null;
		}
		return prevMonitorInfo.status();
	}

	private NodeWithMonitorsInfo initStatus(ProjectJson project, String nodeName, String alias) {
		ProjectStatus projectStatus = project_name_to_status().get(project.name());
		if (null == projectStatus){
			List<NodeWithMonitorsInfo> nodesInfo = Lists.newArrayList();
			projectStatus = new ProjectStatus(project.name(), nodesInfo);
			addProjectStatus(project.name(), projectStatus);
		}
		NodeWithMonitorsInfo nodeInfo = projectStatus.nodeInfoOrNull(nodeName);
		if (null == nodeInfo){
			PeerStatusJsonV2 createJson = createJson();
			Map<String, MonitorInfo> monitors = Maps.newHashMap();
			nodeInfo = new NodeWithMonitorsInfo(createJson.host_port(), nodeName, alias, project.name(), monitors);
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
		return new PeerStatusJsonV2(InetUtils.getLocalHost().getHostName(), codeineRuntimeInfo.port(), codeineRuntimeInfo.version(), codeineRuntimeInfo.startTime(), Constants.getInstallDir(), PathHelper.getTarFile(),project_name_to_status);
	}
	
}
