package codeine.jsons.command;

import java.util.Map;

import com.google.common.collect.Maps;

public class CommandInfoForSpecificNode {

	private String node_name;
	private String node_alias;
	private String tmp_dir;
	private String key;
	private Map<String, String> environment_variables = Maps.newHashMap();
	
	public CommandInfoForSpecificNode(String node_name, String node_alias, String tmp_dir, String key, Map<String, String> environment_variables) {
		super();
		this.node_name = node_name;
		this.node_alias = node_alias;
		this.tmp_dir = tmp_dir;
		this.key = key;
		this.environment_variables = environment_variables;
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
	public Map<String, String> environment_variables() {
		return environment_variables;
	}
	
}
