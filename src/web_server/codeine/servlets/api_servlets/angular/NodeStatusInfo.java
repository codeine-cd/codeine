package codeine.servlets.api_servlets.angular;

import java.util.List;

import codeine.api.NodeWithMonitorsInfo;
import codeine.jsons.peer_status.PeerStatusString;

@SuppressWarnings("unused")
public class NodeStatusInfo {
	private String node_alias;
	private String node_name;
	private List<String> failed_monitors;
	private List<String> ok_monitors;
	private List<String> tags;
	private String peer_key;
	private PeerStatusString peer_status;
	private String version;

	public NodeStatusInfo(NodeWithMonitorsInfo nodeWithMonitorsInfo) {
		super();
		this.node_alias = nodeWithMonitorsInfo.node_alias();
		this.node_name = nodeWithMonitorsInfo.name();
		this.failed_monitors = nodeWithMonitorsInfo.failed_monitors();
		this.ok_monitors = nodeWithMonitorsInfo.ok_monitors();
		this.tags = nodeWithMonitorsInfo.tags();
		this.peer_key = nodeWithMonitorsInfo.peer_key();
		this.peer_status = nodeWithMonitorsInfo.peer().status();
		this.version = nodeWithMonitorsInfo.version();
	}

	public List<String> failed_monitors() {
		return failed_monitors;
	}
	
}