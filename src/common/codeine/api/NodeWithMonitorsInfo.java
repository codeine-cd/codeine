package codeine.api;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import codeine.jsons.peer_status.PeerStatusJsonV2;
import codeine.model.Constants;
import codeine.utils.StringUtils;

import com.google.common.collect.Lists;

public class NodeWithMonitorsInfo extends NodeWithPeerInfo {

	private String projectName;
	private Map<String, MonitorStatusInfo> monitors;
	private String version = Constants.NO_VERSION;
	

	public NodeWithMonitorsInfo(PeerStatusJsonV2 peer, String name, String alias, String projectName, Map<String,MonitorStatusInfo> monitors) {
		super(name, alias, peer);
		this.projectName = projectName;
		this.monitors = monitors;
	}
	
	public NodeWithMonitorsInfo(String name, String alias, String projectName, Map<String,MonitorStatusInfo> monitors, String version) {
		this(null, name, alias, projectName, monitors);
		this.version = version;
	}
	
	public NodeWithMonitorsInfo(NodeWithMonitorsInfo node) {
		this(node.peer(),node.name(), node.alias(), node.projectName(), node.monitors());
		this.version = node.version();
		this.tags(node.tags());
	}
	
	public String projectName() {
		return projectName;
	}
	
	public Map<String, MonitorStatusInfo> monitors() {
		return monitors;
	}

	public boolean status() {
		for (MonitorStatusInfo m : monitors.values()) {
			if (m.fail()){
				return false;
			}
		}
		return true;
	}
	
	public String version() {
		if (Constants.NO_VERSION != versionOld() && !StringUtils.isEmpty(versionOld()) ) {
			return versionOld();
		}
		if (Constants.NO_VERSION != version && !StringUtils.isEmpty(version)) {
			return version;
		}
		return Constants.NO_VERSION;
	}
	//TODO remove after build 1100
	private String versionOld() {
		MonitorStatusInfo version = monitors.get("version");
		if (version == null){
			return Constants.NO_VERSION;
		}
		return version.status();
	}
	public String version(String version) {
		String prevVersion = this.version;
		this.version = version;
		return prevVersion;
	}

	
	public List<String> failedMonitors() {
		List<String> $ = Lists.newArrayList();
		for (Entry<String, MonitorStatusInfo> monitor : monitors.entrySet()) {
			if (monitor.getValue().fail())
				$.add(monitor.getKey());
		}
		return $;
	}

	
	public List<String> ok_monitors() {
		List<String> $ = Lists.newArrayList();
		for (MonitorStatusInfo m : monitors.values()) {
			if (!m.fail()){
				$.add(m.name());
			}
		}
		return $;
	}

}
