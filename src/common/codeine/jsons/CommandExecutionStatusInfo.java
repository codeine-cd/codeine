package codeine.jsons;

import java.util.List;

import codeine.api.NodeWithPeerInfo;
import codeine.jsons.command.CommandParameterInfo;

import com.google.common.collect.Lists;

public class CommandExecutionStatusInfo {

	private String command;
	private List<CommandParameterInfo> params;
	private String project_name;
	private List<NodeWithPeerInfo> nodes_list;
	private List<NodeWithPeerInfo> fail_list = Lists.newArrayList();
	private List<NodeWithPeerInfo> success_list = Lists.newArrayList();
	private long start_time;
	private long id;
	private boolean finished;
	
	public CommandExecutionStatusInfo() {
		super();
	}


	public CommandExecutionStatusInfo(String command, List<CommandParameterInfo> params, String projectName, List<NodeWithPeerInfo> nodes_list, long id) {
		this.command = command;
		this.params = params;
		this.project_name = projectName;
		this.nodes_list = nodes_list;
		this.id = id;
		this.start_time = System.currentTimeMillis();
	}


	public void addFailedNode(NodeWithPeerInfo node) {
		fail_list.add(node);
	}


	public void addSuccessNode(NodeWithPeerInfo node) {
		success_list.add(node);
	}


	public List<NodeWithPeerInfo> fail_list() {
		return fail_list;
	}
	public List<NodeWithPeerInfo> success_list() {
		return success_list;
	}
	public List<NodeWithPeerInfo> nodes_list() {
		return nodes_list;
	}


	public String command() {
		return command;
	}
	
	public long start_time() {
		return start_time;
	}
	
	public long id() {
		return id;
	}

	public boolean finished() {
		return finished;
	}
	
	public void finish() {
		finished = true;
	}


	@Override
	public String toString() {
		return "CommandDataJson [command=" + command + ", params=" + params + ", project_name=" + project_name
				+ ", nodes_list=" + nodes_list + ", fail_list=" + fail_list + ", success_list=" + success_list
				+ ", start_time=" + start_time + ", id=" + id + ", finished=" + finished + "]";
	}


	public String project_name() {
		return project_name;
	}

	
}
