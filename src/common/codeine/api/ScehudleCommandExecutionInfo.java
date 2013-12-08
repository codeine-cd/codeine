package codeine.api;

import java.util.List;

import codeine.jsons.command.CommandInfo;


public class ScehudleCommandExecutionInfo {
	
	private CommandInfo command_info;
	private List<NodeWithPeerInfo> nodes;
	
	
	public ScehudleCommandExecutionInfo() {
	}

	private ScehudleCommandExecutionInfo(CommandInfo command_info, List<NodeWithPeerInfo> nodes) {
		this.command_info = command_info;
		this.nodes = nodes;
	}

	public static ScehudleCommandExecutionInfo createImmediate(CommandInfo commandInfo, List<NodeWithPeerInfo> nodes) {
		return new ScehudleCommandExecutionInfo(commandInfo, nodes);
	}
	
	public List<NodeWithPeerInfo> nodes() {
		return nodes;
	}
	public CommandInfo command_info() {
		return command_info;
	}
}