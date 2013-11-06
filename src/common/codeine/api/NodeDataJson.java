package codeine.api;

public class NodeDataJson {
	
	private String peer_address, node_name, node_alias;
	
	public NodeDataJson() {
		
	}
	
	

	public NodeDataJson(String peer_address, String node_name, String node_alias) {
		super();
		this.peer_address = peer_address;
		this.node_name = node_name;
		this.node_alias = node_alias;
	}



	public String peer_address() {
		return peer_address;
	}
	public String node_name() {
		return node_name;
	}
	public String node_alias() {
		return node_alias;
	}
}