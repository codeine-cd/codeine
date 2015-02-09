package codeine.jsons.mails;

import codeine.api.NodeInfo;
import codeine.utils.StringUtils;

public class CollectorNotificationJson {

	private String collector_name;
	private String project_name;
	private NodeInfo node;
	private String output;
	private long time;
	private String time_formatted;
	private long collection_type;
	private long collection_type_update_time;
	private String version;
	private String peer;
	private Integer exit_status;
	private String duration;
	//TODO remove
	private boolean is_for_collector;
	private String notification_id;
	private int notifications_in_24h;
	public CollectorNotificationJson(){
		
	}
	
	public CollectorNotificationJson(String collector_name, String project_name, String output, NodeInfo node, String version, String peer, int exit_status, String duration, boolean is_for_collector, int notifications_in_24h) {
		this.collector_name = collector_name;
		this.project_name = project_name;
		this.output = output;
		this.node = node;
		this.version = version;
		this.exit_status = exit_status;
		this.duration = duration;
		this.time = System.currentTimeMillis();
		this.time_formatted = StringUtils.formatDate(this.time);
		this.collection_type = AlertsCollectionType.NotCollected.toLong();
		this.peer = peer;
		this.is_for_collector = is_for_collector;
		notification_id = project_name + "_" + node.name() + "_" + collector_name;
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

	public long collection_type() {
		return collection_type;
	}
	
	public NodeInfo node(){
		return node;
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
				+ ", collection_type=" + collection_type + ", collection_type_update_time="
				+ collection_type_update_time + ", version=" + version + ", peer=" + peer + ", exit_status="
				+ exit_status + ", duration=" + duration + "]";
	}

	public String toStringNoOutput() {
		return "CollectorNotificationJson [collector_name=" + collector_name + ", project_name=" + project_name
				+ ", node=" + node + ", time=" + time + ", collection_type=" + collection_type
				+ ", collection_type_update_time=" + collection_type_update_time 
				+ ", version=" + version 
				+ ", exit_status=" + exit_status 
				+ ", duration=" + duration 
				+ ", peer="
				+ peer + "]";
	}

	public boolean is_for_collector() {
		return is_for_collector;
	}

	public String notification_id() {
		return notification_id;
	}
	public int notifications_in_24h() {
		return notifications_in_24h;
	}

	
}
