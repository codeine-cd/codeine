package codeine.command_peer;

import java.util.List;

import org.apache.log4j.Logger;

import codeine.api.NodeWithPeerInfo;
import codeine.api.ScehudleCommandExecutionInfo;
import codeine.configuration.Links;
import codeine.jsons.project.ProjectJson;
import codeine.permissions.IUserWithPermissions;

public class ImmediatlyCommandStrategy extends CommandExecutionStrategy {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(ImmediatlyCommandStrategy.class);
	

	public ImmediatlyCommandStrategy(AllNodesCommandExecuter allNodesCommandExecuter,
			ScehudleCommandExecutionInfo commandData, Links links, ProjectJson project,
		IUserWithPermissions userObject, long commandId) {
		super(commandData, allNodesCommandExecuter, links, project, userObject, commandId);
	}

	@Override
	public void execute() {
		List<NodeWithPeerInfo> nodes = commandData().nodes();
		int concurrency = commandData().command_info().concurrency();
		executeConcurrent(nodes, concurrency);
	}

	
}