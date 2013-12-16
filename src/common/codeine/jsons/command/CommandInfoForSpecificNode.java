package codeine.jsons.command;

public class CommandInfoForSpecificNode {

	private String node_name;
	private String node_alias;

	public CommandInfoForSpecificNode(String node_name, String node_alias) {
		super();
		this.node_name = node_name;
		this.node_alias = node_alias;
	}
	
	public String node_alias() {
		return node_alias;
	}
	public String node_name() {
		return node_name;
	}

}
