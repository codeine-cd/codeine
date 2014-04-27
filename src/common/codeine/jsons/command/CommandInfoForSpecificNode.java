package codeine.jsons.command;

public class CommandInfoForSpecificNode {

	private String node_name;
	private String node_alias;
	private String tmp_dir;
	private String key;
	
	public CommandInfoForSpecificNode(String node_name, String node_alias, String tmp_dir, String key) {
		super();
		this.node_name = node_name;
		this.node_alias = node_alias;
		this.tmp_dir = tmp_dir;
		this.key = key;
	}
	public String node_alias() {
		return node_alias;
	}
	public String node_name() {
		return node_name;
	}
	public String tmp_dir() {
		return tmp_dir;
	}
	public String key() {
		return key;
	}
}
