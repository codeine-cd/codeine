package codeine.api;

import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import codeine.jsons.command.CommandParameterInfo;
import codeine.utils.StringUtils;

import com.google.common.collect.Lists;

/**
 * This class represents the data saved for each command on disk, and send to ui
 */
@SuppressWarnings("unused")
public class CommandExecutionStatusInfo {

	private String command;
	private List<CommandParameterInfo> params;
	private String project_name;
	private List<NodeInfoNameAndAlias> nodes_list;
	private Queue<NodeInfoNameAndAlias> fail_list = new ConcurrentLinkedDeque<NodeInfoNameAndAlias>();
	private Queue<NodeInfoNameAndAlias> success_list = new ConcurrentLinkedDeque<NodeInfoNameAndAlias>();
	private long start_time;
	private Long finish_time;
	private String user = "Guest";
	private long id;
	private boolean finished;
	private String output;
	private boolean can_cancel;
	private boolean can_rerun;
	
	public CommandExecutionStatusInfo() {
		super();
	}

	public CommandExecutionStatusInfo(String user, String command, List<CommandParameterInfo> params, String projectName, List<? extends NodeInfoNameAndAlias> nodes_list, long id) {
		this.command = command;
		this.params = params;
		this.project_name = projectName;
		this.nodes_list = copyNodesInfo(nodes_list);
		this.id = id;
		this.start_time = System.currentTimeMillis();
		this.user = StringUtils.isEmpty(user) ? "Guest" : user; 
	}

	private List<NodeInfoNameAndAlias> copyNodesInfo(List<? extends NodeInfoNameAndAlias> nodes_list) {
		List<NodeInfoNameAndAlias> $ = Lists.newArrayList();
		for (NodeInfoNameAndAlias nodeWithPeerInfo : nodes_list) {
			$.add(copyOnlyNodeInfo(nodeWithPeerInfo));
		}
		return $;
	}

	private NodeInfoNameAndAlias copyOnlyNodeInfo(NodeInfoNameAndAlias nodeWithPeerInfo) {
		NodeInfoNameAndAlias n = null;
		if (nodeWithPeerInfo.getClass().equals(NodeInfoNameAndAlias.class)) {
			n = nodeWithPeerInfo;
		}
		else {
			n = new NodeInfoNameAndAlias(nodeWithPeerInfo);
		}
		return n;
	}

	public void addFailedNode(NodeInfoNameAndAlias node) {
		fail_list.add(copyOnlyNodeInfo(node));
	}

	public void addSuccessNode(NodeInfoNameAndAlias node) {
		success_list.add(copyOnlyNodeInfo(node));
	}

	public Collection<NodeInfoNameAndAlias> fail_list() {
		return fail_list;
	}
	public Collection<NodeInfoNameAndAlias> success_list() {
		return success_list;
	}
	public List<NodeInfoNameAndAlias> nodes_list() {
		return nodes_list;
	}

	public String user() {
		return user;
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
	
	public void finish_time(long finish_time) {
		this.finish_time = finish_time;
	}
	
	public void start_time(long start_time) {
		this.start_time = start_time;
	}
	
	public long finishTimeForRemoval() {
		return finish_time == null ? start_time : finish_time;
	}
	
	public void finish() {
		finished = true;
		finish_time = System.currentTimeMillis();
	}

	public String project_name() {
		return project_name;
	}

	public void output(String output) {
		this.output = output;
		
	}

	@Override
	public String toString() {
		return "CommandExecutionStatusInfo [project_name=" + project_name + ", command=" + command + ", id=" + id
				+ ", finished=" + finished + "]";
	}

	public String command_name() {
		return command();
	}

	public void clearPasswordParams() {
		for (CommandParameterInfo parameter : params) {
			parameter.clearPassword();
		}
	}

	public void can_cancel(boolean can_cancel) {
		this.can_cancel = can_cancel;
	}
	public void can_rerun(boolean can_rerun) {
		this.can_rerun = can_rerun;
	}
}
