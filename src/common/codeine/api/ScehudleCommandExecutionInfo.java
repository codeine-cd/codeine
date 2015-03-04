package codeine.api;

import java.util.List;

import codeine.jsons.command.CommandInfo;

import com.google.common.collect.Lists;


public class ScehudleCommandExecutionInfo {
	
	private CommandInfo command_info;
	private List<NodeWithPeerInfo> nodes = Lists.newArrayList();
	private List<String> node_name_list = Lists.newArrayList();
	private boolean should_execute_on_all_nodes;
	private String address_to_notify;
	
	
	public ScehudleCommandExecutionInfo() {
	}

	public ScehudleCommandExecutionInfo(CommandInfo command_info, List<NodeWithPeerInfo> nodes, boolean should_execute_on_all_nodes) {
		this.command_info = command_info;
		this.nodes = nodes;
		this.should_execute_on_all_nodes = should_execute_on_all_nodes;
	}

	public static ScehudleCommandExecutionInfo createImmediate(CommandInfo commandInfo, List<NodeWithPeerInfo> nodes, boolean should_execute_on_all_nodes) {
		return new ScehudleCommandExecutionInfo(commandInfo, nodes, should_execute_on_all_nodes);
	}
	
	public List<NodeWithPeerInfo> nodes() {
		return nodes;
	}
	public List<String> node_name_list() {
		return node_name_list;
	}
	public boolean should_execute_on_all_nodes() {
		return should_execute_on_all_nodes;
	}
	public CommandInfo command_info() {
		return command_info;
	}

	public String address_to_notify() {
		return address_to_notify;
	}
}