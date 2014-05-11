package codeine.command_peer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import codeine.api.CommandExcutionType;
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
import codeine.statistics.IMonitorStatistics;
import codeine.utils.ExceptionUtils;
import codeine.utils.FilesUtils;
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
	
	private int total;
	private int count;
	private int fail;
	private BufferedWriter writer;
	private boolean active = true;
	private long dirName;
	private String dirNameFull;
	private ScehudleCommandExecutionInfo commandData;
	private CommandExecutionStatusInfo commandDataJson;
	private ProjectJson project;
	private IUserWithPermissions userObject;
	private Object fileWriteSync = new Object();
	private CommandExecutionStrategy strategy;

	public long executeOnAllNodes(IUserWithPermissions userObject, ScehudleCommandExecutionInfo commandData, ProjectJson project) {
		this.project = project;
		this.userObject = userObject;
		try {
			this.commandData = commandData;
			this.total = commandData.nodes().size();
			dirName = getNewDirName();
			dirNameFull = pathHelper.getCommandOutputDir(commandData.command_info().project_name(), String.valueOf(dirName));
			FilesUtils.mkdirs(dirNameFull);
			String pathname = dirNameFull + "/log";
			File file = new File(pathname);
			FilesUtils.createNewFile(file);
			createCommandDataFile(userObject.user().username());
			writer = TextFileUtils.getWriter(file, false);
			log.info("running command " + commandData.command_info().command_name() + " with concurrency " + commandData.command_info().concurrency() + "by " + userObject.user());
			writeLine("running command '"+commandData.command_info().command_name()+"' on " + commandData.nodes().size() + " nodes by " + userObject.user());
			writeNodesList(commandData);
			updatePeersAddresses();
			ThreadUtils.createThread(new Runnable() {
				@Override
				public void run() {
					execute();
				};
			}, "AllNodesCommandExecuter_"+commandData.command_info().command_name()).start();
			return dirName;
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
		if (null != commandDataJson) {
			commandDataJson.finish();
			monitorsStatistics.updateCommand(commandDataJson);
		}
		try {
			updateJson();
			FilesUtils.createNewFile(dirNameFull + Constants.COMMAND_FINISH_FILE);
		} catch (Exception e) {
			log.warn("Failed to mark command as finished " + commandDataJson, e);
		}
		active = false;
	}

	private void execute() {
		try {
			if (commandData.command_info().command_strategy() == CommandExcutionType.Immediately){
				strategy = new ImmediatlyCommandStrategy(this, commandData, links,project, userObject);
			}
			else { 
				strategy = new ProgressiveExecutionStrategy(this, commandData, links, nodeGetter,project, userObject);
			}
			strategy.execute();
			if (strategy.isCancel()) {
				writeLine("command was canceled by user");
			}
			writeFooter();
		} finally {
			finish();
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
		if (!commandDataJson.fail_list().isEmpty()) {
			writeLine("failed nodes: " + StringUtils.collectionToString(commandDataJson.fail_list(), f));
		}
		writeLine("=========> aggregate-command-statistics (fail/total): " + fail + "/" + total + "\n");
	}

	private void createCommandDataFile(String user) {
		commandDataJson = new CommandExecutionStatusInfo(user, commandData.command_info().command_name(), commandData.command_info().parameters(), commandData.command_info().project_name(),
				commandData.nodes(), dirName);
		FilesUtils.createNewFile(commandFile());
		updateJson();
	}

	private String commandFile() {
		return dirNameFull + Constants.JSON_COMMAND_FILE_NAME;
	}

	private long getNewDirName() {
		long i = 0;
		String dir = pathHelper.getPluginsOutputDir(commandData.command_info().project_name());
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
		synchronized (commandDataJson) {
			commandDataJson.addFailedNode(node);
		}
	}

	public void nodeSuccess(NodeWithPeerInfo node) {
		log.debug("node success " + node.name());
		synchronized (commandDataJson) {
			commandDataJson.addSuccessNode(node);
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
		synchronized (commandDataJson) {
			json = gson.toJson(commandDataJson);
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
		return commandDataJson;
	}

	public long id() {
		return dirName;
	}

	public void cancel() {
		strategy.setCancel();
	}
}