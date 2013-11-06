package codeine.command_peer;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import codeine.api.NodeDataJson;
import codeine.configuration.Links;

public abstract class CommandExecutionStrategy {

	private static final Logger log = Logger.getLogger(CommandExecutionStrategy.class);
	private ScehudleCommandPostData commandData;
	private AllNodesCommandExecuter allNodesCommandExecuter;
	private Links links;
	private boolean cancel;
	
	public CommandExecutionStrategy(AllNodesCommandExecuter allNodesCommandExecuter,ScehudleCommandPostData commandData, Links links) {
		this.allNodesCommandExecuter = allNodesCommandExecuter;
		this.commandData = commandData;
		this.links = links;
	}
	
	public abstract void execute();
	
	protected void writeLine(String message) {
		allNodesCommandExecuter.writeLine(message);
	}
	
	private void commandNode(ExecutorService executor, NodeDataJson hostport) {
		String link = links.getPeerCommandLink(hostport.peer_address(), commandData.project_name(), commandData.command(),commandData.params());
		log.info("commandNode link is " + link);
		PeerCommandWorker worker = new PeerCommandWorker(link, hostport, allNodesCommandExecuter);
		executor.execute(worker);
	}
	
	protected ScehudleCommandPostData commandData() {
		return commandData;
	}

	protected void executeConcurrent(List<NodeDataJson> nodes, int concurrency) {
		ExecutorService executor = Executors.newFixedThreadPool(concurrency);
		for (NodeDataJson peer : nodes) {
			commandNode(executor, peer);
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