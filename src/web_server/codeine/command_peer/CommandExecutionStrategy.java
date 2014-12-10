package codeine.command_peer;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import codeine.api.NodeWithPeerInfo;
import codeine.api.ScehudleCommandExecutionInfo;
import codeine.configuration.Links;
import codeine.jsons.project.ProjectJson;
import codeine.permissions.IUserWithPermissions;

public abstract class CommandExecutionStrategy {

	private static final Logger log = Logger.getLogger(CommandExecutionStrategy.class);
	
	private ScehudleCommandExecutionInfo commandData;
	private AllNodesCommandExecuter allNodesCommandExecuter;
	private Links links;
	private boolean cancel;
	private ProjectJson project;
	private IUserWithPermissions userObject;
	private String error;
	
	public CommandExecutionStrategy(ScehudleCommandExecutionInfo commandData,
			AllNodesCommandExecuter allNodesCommandExecuter, Links links, ProjectJson project, IUserWithPermissions userObject) {
		super();
		this.commandData = commandData;
		this.allNodesCommandExecuter = allNodesCommandExecuter;
		this.links = links;
		this.project = project;
		this.userObject = userObject;
	}

	public abstract void execute();
	
	protected void writeLine(String message) {
		allNodesCommandExecuter.writeLine(message);
	}
	
	private void commandNode(ExecutorService executor, NodeWithPeerInfo node, boolean shouldOutputImmediatly) {
		PeerCommandWorker worker = new PeerCommandWorker(node, allNodesCommandExecuter, commandData.command_info(), shouldOutputImmediatly, links, project, userObject);
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
		while (!executor.isTerminated() && !isCancel()) {
			try {
				Thread.sleep(1000);
				if (isCancel()) {
					List<Runnable> shutdownNow = executor.shutdownNow();
					log.info("shutdownNow " + shutdownNow.size());
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

	public boolean isError() {
		return error != null;
	}

	public String error() {
		return error;
	}
	
	protected void error(String error) {
		this.error = error;
	}
}