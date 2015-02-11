package codeine.jsons.mails;

import codeine.api.NodeInfo;
import codeine.utils.StringUtils;

public class CollectorNotificationJson {

	private String collector_name;
	private String project_name;
	//TODO remove after peers in version > 1062
	private NodeInfo node;
	private String node_name;
	private String node_alias;
	private String output;
	private long time;
	private String time_formatted;
	private String version;
	private String peer;
	private Integer exit_status;
	private String duration;
	private String notification_id;
	private int notifications_in_24h;
	public CollectorNotificationJson(){
		
	}
	
	public CollectorNotificationJson(String collector_name, String project_name, String output, String node_name, String node_alias, String version, String peer, int exit_status, String duration, boolean is_for_collector, int notifications_in_24h) {
		this.collector_name = collector_name;
		this.project_name = project_name;
		this.output = output;
		this.node_name = node_name;
		this.node_alias = node_alias;
		this.version = version;
		this.exit_status = exit_status;
		this.duration = duration;
		this.time = System.currentTimeMillis();
		this.time_formatted = StringUtils.formatDate(this.time);
		this.peer = peer;
		notification_id = project_name + "_" + node_name + "_" + collector_name;
		this.notifications_in_24h = notifications_in_24h;
	}

	public String collector_name() {
		return collector_name;
	}

	public String project_name() {
		return project_name;
	}

	public String output() {
		return output;
	}

	public long time() {
		return time;
	}

	public String node_name(){
		if (null != node) {
			return node.name();
		}
		return node_name;
	}
	public String node_alias(){
		if (null != node) {
			return node.alias();
		}
		return node_alias;
	}

	public String version() {
		return version;
	}

	public String peer() {
		return peer;
	}
	public Integer exit_status() {
		return exit_status;
	}
	public String time_formatted() {
		return time_formatted;
	}
	public String duration() {
		return duration;
	}

	@Override
	public String toString() {
		return "CollectorNotificationJson [collector_name=" + collector_name + ", project_name=" + project_name
				+ ", node=" + node + ", output=" + output + ", time=" + time + ", time_formatted=" + time_formatted
				+ ", version=" + version + ", peer=" + peer + ", exit_status="
				+ exit_status + ", duration=" + duration + "]";
	}

	public String toStringNoOutput() {
		return "CollectorNotificationJson [collector_name=" + collector_name + ", project_name=" + project_name
				+ ", node_name=" + node_name + ", time=" + time + ", version=" + version 
				+ ", exit_status=" + exit_status 
				+ ", duration=" + duration 
				+ ", peer="
				+ peer + "]";
	}

	public String notification_id() {
		return notification_id;
	}
	public int notifications_in_24h() {
		return notifications_in_24h;
	}

	
}
