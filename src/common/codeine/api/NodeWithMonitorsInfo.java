package codeine.api;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import codeine.model.Constants;

import com.google.common.collect.Lists;

public class NodeWithMonitorsInfo extends NodeWithPeerInfo {

	private String projectName;
	private Map<String, MonitorInfo> monitors;
	

	public NodeWithMonitorsInfo(String peerName, String name, String alias, String projectName, Map<String,MonitorInfo> monitors) {
		super(name, alias, peerName);
		this.projectName = projectName;
		this.monitors = monitors;
	}
	
	public String projectName() {
		return projectName;
	}
	
	public Map<String, MonitorInfo> monitors() {
		return monitors;
	}

	public boolean status() {
		for (MonitorInfo m : monitors.values()) {
			if (m.fail()){
				return false;
			}
		}
		return true;
	}
	
	public String version() {
		MonitorInfo version = monitors.get("version");
		if (version == null){
			return Constants.NO_VERSION;
		}
		return version.status();
	}

	public List<String> failedMonitors() {
		List<String> $ = Lists.newArrayList();
		for (Entry<String, MonitorInfo> monitor : monitors.entrySet()) {
			if (monitor.getValue().fail())
				$.add(monitor.getKey());
		}
		return $;
	}

	
}
