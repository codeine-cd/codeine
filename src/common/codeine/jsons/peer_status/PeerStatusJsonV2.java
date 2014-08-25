package codeine.jsons.peer_status;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import codeine.api.MonitorStatusInfo;
import codeine.api.NodeWithMonitorsInfo;
import codeine.model.Constants;
import codeine.utils.StringUtils;
import codeine.utils.network.HttpUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@SuppressWarnings("unused")
public class PeerStatusJsonV2 {
	private String peer_key;
	//TODO remove after cf-engine in build > 1.1.309
	private String peer_old_key;
	private String peer_host_port;
	private String peer_ip;
	private String user_dns_domain;
	private Map<String, ProjectStatus> project_name_to_status = Maps.newHashMap();//Lists.newArrayList();
	private String host;
	private int port;
	private String version;
	private String tar;
	private long start_time;
	private long update_time;//updated in directory server when first seen
	private long update_time_from_peer;
	private String install_dir;
	private PeerType peer_type;
	private transient PeerStatusString status;
	
	public PeerStatusJsonV2(String host, int port, String version, long start_time, String install_dir, String tar, Map<String, ProjectStatus> project_name_to_status, String peer_ip, String user_dns_domain) {
		super();
		this.host = host;
		this.port = port;
		this.peer_ip = peer_ip;
		this.version = version;
		this.start_time = start_time;
		this.install_dir = install_dir;
		this.tar = tar;
		this.project_name_to_status = Maps.newHashMap(project_name_to_status);
		this.peer_old_key = host + ":" + install_dir;
		this.peer_key = host + ":" + HttpUtils.specialEncode(install_dir);
		this.peer_host_port = host + ":" + port;
		this.user_dns_domain = user_dns_domain;
		this.peer_type = PeerType.Daemon;
		this.project_name_to_status.put(Constants.CODEINE_NODES_PROJECT_NAME, createInternalProject());
		this.update_time = System.currentTimeMillis();
		this.update_time_from_peer = System.currentTimeMillis();
	}
	private ProjectStatus createInternalProject() {
		NodeWithMonitorsInfo node_info = new NodeWithMonitorsInfo(this, this.peer_key, this.host, Constants.CODEINE_NODES_PROJECT_NAME, Maps.<String, MonitorStatusInfo>newHashMap());
		node_info.version(this.version);
		node_info.tags(Lists.newArrayList(project_name_to_status.keySet()));
		ProjectStatus ps = new ProjectStatus(Constants.CODEINE_NODES_PROJECT_NAME, node_info);
		return ps;
	}	
	public PeerStatusJsonV2(String peer_key, ProjectStatus projectStatus) {
		super();
		this.project_name_to_status = Maps.newHashMap();
		this.project_name_to_status.put(projectStatus.project_name(), projectStatus);
		this.update_time = System.currentTimeMillis();
		this.update_time_from_peer = System.currentTimeMillis();
		this.peer_key = peer_key;
		this.peer_type = PeerType.Reporter;
	}	
	
	public void addProjectStatus(String name, ProjectStatus status) {
		HashMap<String, ProjectStatus> tempList = Maps.newHashMap(project_name_to_status);
		tempList.put(name, status);
		project_name_to_status = tempList;
	}
	
	public Map<String, ProjectStatus> project_name_to_status() {
		return Collections.unmodifiableMap(project_name_to_status);
	}
	
	public String peer_key() {
		return peer_key;
	}
	
	public String host_port() {
		return host + ":" + port;
	}
	public String ip_port() {
		return peer_ip + ":" + port;
	}
	public String address_port() {
		return StringUtils.isEmpty(user_dns_domain) ? host_port() : host + "." + user_dns_domain + ":" + port;
	}

	public long update_time() {
		return update_time;
	}
	public long update_time_from_peer() {
		return update_time_from_peer;
	}

	public String key() {
		return peer_key();
	}

	public String version() {
		return version;
	}

	public String host() {
		return host;
	}

	public String tar() {
		return tar;
	}
	public void status(PeerStatusString status) {
		this.status = status;
	}
	public PeerStatusString status() {
		return status;
	}
	public void updateNodesWithPeer() {
		for (ProjectStatus projectStatus : project_name_to_status.values()) {
			projectStatus.updateNodesWithPeer(this);
		}
	}
	public PeerType peer_type() {
		return peer_type;
	}
	public String peer_old_key() {
		return peer_old_key;
	}
	@Override
	public String toString() {
		return "PeerStatusJsonV2 [host_port()=" + host_port() + ", update_time()=" + new Date(update_time())
				+ ", update_time_from_peer()=" + new Date(update_time_from_peer()) + ", peer_type()=" + peer_type() + "]";
	}
	
}
