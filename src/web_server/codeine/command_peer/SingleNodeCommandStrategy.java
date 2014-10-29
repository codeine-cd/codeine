package codeine.command_peer;

import java.util.List;

import org.apache.log4j.Logger;

import codeine.api.NodeWithPeerInfo;
import codeine.api.ScehudleCommandExecutionInfo;
import codeine.configuration.Links;
import codeine.jsons.project.ProjectJson;
import codeine.permissions.IUserWithPermissions;

public class SingleNodeCommandStrategy extends CommandExecutionStrategy {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(SingleNodeCommandStrategy.class);
	

	public SingleNodeCommandStrategy(AllNodesCommandExecuter allNodesCommandExecuter,
			ScehudleCommandExecutionInfo commandData, Links links, ProjectJson project, IUserWithPermissions userObject) {
		super(commandData, allNodesCommandExecuter, links, project, userObject);
	}

	@Override
	public void execute() {
		List<NodeWithPeerInfo> nodes = commandData().nodes();
		if (nodes.size() > 1) {
			error("ERROR: cannot execute command on more than one node");
			return;
		}
		executeConcurrent(nodes, 1);
	}

	
}