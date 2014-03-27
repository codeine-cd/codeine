package codeine.command_peer;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import codeine.api.NodeWithPeerInfo;
import codeine.api.ScehudleCommandExecutionInfo;
import codeine.configuration.Links;
import codeine.jsons.project.ProjectJson;

public abstract class CommandExecutionStrategy {

	private ScehudleCommandExecutionInfo commandData;
	private AllNodesCommandExecuter allNodesCommandExecuter;
	private Links links;
	private boolean cancel;
	private ProjectJson project;
	
	public CommandExecutionStrategy(AllNodesCommandExecuter allNodesCommandExecuter,ScehudleCommandExecutionInfo commandData, Links links, ProjectJson project) {
		this.allNodesCommandExecuter = allNodesCommandExecuter;
		this.commandData = commandData;
		this.links = links;
		this.project = project;
	}
	
	public abstract void execute();
	
	protected void writeLine(String message) {
		allNodesCommandExecuter.writeLine(message);
	}
	
	private void commandNode(ExecutorService executor, NodeWithPeerInfo node, boolean shouldOutputImmediatly) {
		PeerCommandWorker worker = new PeerCommandWorker(node, allNodesCommandExecuter, commandData.command_info(), shouldOutputImmediatly, links);
		executor.execute(worker);
	}
	
	protected ScehudleCommandExecutionInfo commandData() {
		return commandData;
	}

	protected void executeConcurrent(List<NodeWithPeerInfo> nodes, int concurrency) {
		boolean shouldOutputImmediatly = concurrency < 2 || nodes.size() < 2;
		ExecutorService executor = Executors.newFixedThreadPool(concurrency);
		for (NodeWithPeerInfo peer : nodes) {
			commandNode(executor, peer, shouldOutputImmediatly);
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
			try {
				Thread.sleep(1000);
				if (isCancel()) {
					executor.shutdownNow();
				}
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void setCancel() {
		cancel = true;
	}
	
	public boolean isCancel(){
		return cancel;
	}
	
}