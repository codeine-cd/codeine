package codeine.jsons.mails;

import codeine.api.NodeInfo;

public class CollectorNotificationJson {

	private String collector_name;
	private String project_name;
	private NodeInfo node;
	private String output;
	private long time;
	private long collection_type;
	private long collection_type_update_time;
	private String version;
	private String peer;

	public CollectorNotificationJson(){
		
	}
	
	public CollectorNotificationJson(String collector_name, String project_name, String output, NodeInfo node, String version, String peer) {
		this.collector_name = collector_name;
		this.project_name = project_name;
		this.output = output;
		this.node = node;
		this.version = version;
		this.time = System.currentTimeMillis();
		this.collection_type = AlertsCollectionType.NotCollected.toLong();
		this.peer = peer;
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

	@Override
	public String toString() {
		return "CollectorNotificationJson [collector_name=" + collector_name + ", project_name=" + project_name
				+ ", node=" + node + ", output=" + output + ", time=" + time + ", collection_type=" + collection_type
				+ ", collection_type_update_time=" + collection_type_update_time + ", version=" + version + ", peer="
				+ peer + "]";
	}

	
}
