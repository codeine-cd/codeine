package codeine.command_peer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import codeine.api.NodeGetter;
import codeine.api.NodeWithPeerInfo;
import codeine.api.ScehudleCommandExecutionInfo;
import codeine.configuration.Links;
import codeine.configuration.PathHelper;
import codeine.jsons.CommandExecutionStatusInfo;
import codeine.jsons.peer_status.PeerStatusJsonV2;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.permissions.IUserWithPermissions;
import codeine.plugins.DiscardOldCommandsPlugin;
import codeine.statistics.IMonitorStatistics;
import codeine.utils.ExceptionUtils;
import codeine.utils.FilesUtils;
import codeine.utils.SocketUtils;
import codeine.utils.StringUtils;
import codeine.utils.TextFileUtils;
import codeine.utils.ThreadUtils;

import com.google.common.base.Function;
import com.google.gson.Gson;
import com.google.inject.Inject;

public class AllNodesCommandExecuter {

	private static final Logger log = Logger.getLogger(AllNodesCommandExecuter.class);

	@Inject	private Links links;
	@Inject	private PathHelper pathHelper;
	@Inject	private Gson gson;
	@Inject	private NodeGetter nodeGetter;
	@Inject private IMonitorStatistics monitorsStatistics;
	@Inject private DiscardOldCommandsPlugin discardOldCommandsPlugin;
	
	private int total;
	private int count;
	private int fail;
	private BufferedWriter writer;
	private boolean active = true;
	private long commandId;
	private String dirNameFull;
	private ScehudleCommandExecutionInfo commandData;
	private CommandExecutionStatusInfo commandExecutionInfo;
	private ProjectJson project;
	private IUserWithPermissions userObject;
	private Object fileWriteSync = new Object();
	private CommandExecutionStrategy strategy;

	private String cancelingUser;

	public long executeOnAllNodes(IUserWithPermissions userObject, ScehudleCommandExecutionInfo commandData, ProjectJson project) {
		this.project = project;
		this.userObject = userObject;
		discardOldCommandsPlugin.queueForDelete(project);
		try {
			this.commandData = commandData;
			this.total = commandData.nodes().size();
			commandId = getNewDirName();
			dirNameFull = pathHelper.getCommandOutputDir(commandData.command_info().project_name(), String.valueOf(commandId));
			FilesUtils.mkdirs(dirNameFull);
			String pathname = dirNameFull + "/log";
			File file = new File(pathname);
			FilesUtils.createNewFile(file);
			createCommandDataFile(userObject.user().username());
			writer = TextFileUtils.getWriter(file, false);
			log.info("running command " + commandData.command_info().command_name() + " with concurrency " + commandData.command_info().concurrency() + "by " + userObject.user());
			String nodesWord = commandData.nodes().size() == 1 ? "node" : "nodes";
			writeLine("running command '"+commandData.command_info().command_name()+"' on " + commandData.nodes().size() + " " + nodesWord + " by " + userObject.user().username());
			writeNodesList(commandData);
			updatePeersAddresses();
			Thread commandThread = ThreadUtils.createThread(new Runnable() {
				@Override
				public void run() {
					execute();
				};
			}, "AllNodesCommandExecuter_"+commandData.command_info().command_name());
			commandThread.start();
			monitorsStatistics.updateCommand(commandExecutionInfo);
			return commandId;
		} catch (Exception ex) {
			finish();
			throw ExceptionUtils.asUnchecked(ex);
		}
	}

	private void updatePeersAddresses() {
		for (NodeWithPeerInfo n : commandData.nodes()) {
			PeerStatusJsonV2 p = nodeGetter.peer(n.peer_key());
			if (null == p) {
				writeLine("Warning: ignoring node '" + n.alias() + "' since peer not found");
				continue;
			}
			n.peer_address(p.address_port());
		}
	}

	public void writeNodesList(ScehudleCommandExecutionInfo commandData) {
		if (commandData.nodes().size() < 11) {
			Function<NodeWithPeerInfo, String> predicate = new Function<NodeWithPeerInfo, String>(){
				@Override
				public String apply(NodeWithPeerInfo input) {
					return input.alias();
				}
			};
			writeLine("nodes list: " + StringUtils.collectionToString(commandData.nodes(), predicate));
		}
	}

	private void finish() {
		log.info("Finishing command " + commandExecutionInfo.id());
		if (null != commandExecutionInfo) {
			commandExecutionInfo.finish();
		}
		try {
			updateJson();
			FilesUtils.createNewFile(dirNameFull + Constants.COMMAND_FINISH_FILE);
		} catch (Exception e) {
			log.warn("Failed to mark command as finished " + commandExecutionInfo, e);
		}
		active = false;
	}

	private void execute() {
		try {
			initStrategy();
			strategy.execute();
			if (strategy.isCancel()) {
				writeLine("Execution was canceled by user " + cancelingUser);
			}
			if (strategy.isError()) {
				writeLine(strategy.error());
			}
			writeFooter();
			if (null != commandData.address_to_notify()) {
				int status = fail > 0 ? 1 : 0;
				String message = "command-finished,project=" + commandData.command_info().project_name() + ",id=" + commandId + ",status=" + status;
				log.info("sending finished event: " + message);
				SocketUtils.sendToPort(commandData.address_to_notify(), message);
			}
		} finally {
			finish();
		}
	}

	private void initStrategy() {
		switch (commandData.command_info().command_strategy())
		{
		case Single: {
			strategy = new SingleNodeCommandStrategy(this, commandData, links,project, userObject);
			break;
		}
		case Immediately: {
			strategy = new ImmediatlyCommandStrategy(this, commandData, links,project, userObject);
			break;
		}
		case Progressive: {
			strategy = new ProgressiveExecutionStrategy(this, commandData, links, nodeGetter,project, userObject);
			break;
		}
		default:
			throw new IllegalStateException("couldnt handle strategy " + commandData.command_info().command_strategy());
		}
	}

	private void writeFooter() {
		writeLine("finished!");
		Function<NodeWithPeerInfo, String> f = new Function<NodeWithPeerInfo, String>() {
			@Override
			public String apply(NodeWithPeerInfo n){
				return n.alias();
			}
		};
		if (!commandExecutionInfo.fail_list().isEmpty()) {
			writeLine("failed nodes: " + StringUtils.collectionToString(commandExecutionInfo.fail_list(), f));
		}
		writeLine("=========> aggregate-command-statistics (success/total): " + (total - fail) + "/" + total + "\n");
	}

	private void createCommandDataFile(String user) {
		commandExecutionInfo = new CommandExecutionStatusInfo(user, commandData.command_info().command_name(), commandData.command_info().parameters(), commandData.command_info().project_name(),
				commandData.nodes(), commandId);
		FilesUtils.createNewFile(commandFile());
		updateJson();
	}

	private String commandFile() {
		return dirNameFull + Constants.JSON_COMMAND_FILE_NAME;
	}

	private long getNewDirName() {
		long i = 0;
		String dir = pathHelper.getAllCommandsInProjectOutputDir(commandData.command_info().project_name());
		List<String> filesInDir = FilesUtils.getFilesInDir(dir);
		for (String dir1 : filesInDir) {
			try {
				long j = Long.parseLong(dir1);
				i = Math.max(i, j);
			} catch (NumberFormatException e) {
				log.debug("error parsing " + dir1);
			}
		}
		return i + 1;
	}

	void writeLine(String line) {
		writeLineToFile(line);
	}

	private synchronized void writeLineToFile(String line) {
		try {
			writer.append(line);
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			throw ExceptionUtils.asUnchecked(e);
		}
	}

	public void fail(NodeWithPeerInfo node) {
		log.debug("node fail " + node.name());
		fail++;
		synchronized (commandExecutionInfo) {
			commandExecutionInfo.addFailedNode(node);
		}
	}

	public void nodeSuccess(NodeWithPeerInfo node) {
		log.debug("node success " + node.name());
		synchronized (commandExecutionInfo) {
			commandExecutionInfo.addSuccessNode(node);
		}
	}

	public void workerFinished() {
		count++;
		updateJson();
	}

	public boolean isActive() {
		return active;
	}

	private void updateJson() {
		String json;
		synchronized (commandExecutionInfo) {
			json = gson.toJson(commandExecutionInfo);
		}
		synchronized (fileWriteSync) {
			TextFileUtils.setContents(commandFile(), json);
		}
	}

	public String name() {
		return commandData.command_info().command_name();
	}

	public int success() {
		return (int) (count - fail) * 100 / total;
	}

	public int error() {
		return fail * 100 / total;
	}

	public String project() {
		return commandData.command_info().project_name();
	}

	public int nodes() {
		return commandData.nodes().size();
	}

	public CommandExecutionStatusInfo commandData() {
		return commandExecutionInfo;
	}

	public long id() {
		return commandId;
	}

	public void cancel(String username) {
		strategy.setCancel();
		this.cancelingUser = username;
	}

	public List<NodeWithPeerInfo> nodesList() {
		return commandData.nodes();
	}
	public Object fileWriteSync() {
		return fileWriteSync;
	}

	public CommandExecutionStatusInfo commandExecutionInfo() {
		return commandExecutionInfo;
	}
}
