package codeine.command_peer;

import java.util.List;

import org.apache.log4j.Logger;

import codeine.api.NodeDataJson;
import codeine.configuration.Links;

public class ImmediatlyCommandStrategy extends CommandExecutionStrategy {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(ImmediatlyCommandStrategy.class);
	

	public ImmediatlyCommandStrategy(AllNodesCommandExecuter allNodesCommandExecuter,ScehudleCommandPostData commandData, Links links) {
		super(allNodesCommandExecuter, commandData, links);
	}

	@Override
	public void execute() {
		List<NodeDataJson> nodes = commandData().nodes();
		int concurrency = commandData().concurrency();
		executeConcurrent(nodes, concurrency);
	}

	
}