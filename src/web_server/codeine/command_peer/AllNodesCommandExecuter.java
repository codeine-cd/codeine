package codeine.command_peer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import codeine.api.NodeDataJson;
import codeine.api.NodeGetter;
import codeine.configuration.ConfigurationManager;
import codeine.configuration.Links;
import codeine.configuration.PathHelper;
import codeine.jsons.CommandDataJson;
import codeine.model.Constants;
import codeine.utils.ExceptionUtils;
import codeine.utils.FilesUtils;
import codeine.utils.StringUtils;
import codeine.utils.TextFileUtils;

import com.google.common.base.Function;
import com.google.gson.Gson;
import com.google.inject.Inject;

public class AllNodesCommandExecuter {

	private static final Logger log = Logger.getLogger(AllNodesCommandExecuter.class);

	@Inject	private Links links;
	@Inject	private ConfigurationManager configurationManager;
	@Inject	private PathHelper pathHelper;
	@Inject	private Gson gson;
	@Inject	private NodeGetter nodeGetter;
	private int total;
	private int count;
	private int fail;
	private BufferedWriter writer;
	private boolean active = true;
	private long dirName;
	private String dirNameFull;
	private ScehudleCommandPostData commandData;
	private CommandDataJson commandDataJson;

	private Object fileWriteSync = new Object();

	private CommandExecutionStrategy strategy;

	public long executeOnAllNodes(ScehudleCommandPostData commandData) {
		try {
			this.commandData = commandData;
			this.total = commandData.nodes().size();
			// final int concurrency =
			// configurationManager.getProjectForName(commandData.project_name()).getCommand(commandData.command()).concurrency();
			dirName = getNewDirName();
			dirNameFull = pathHelper.getPluginsOutputDir(commandData.project_name()) + "/" + dirName;
			FilesUtils.mkdirs(dirNameFull);
			String pathname = dirNameFull + "/log";
			File file = new File(pathname);
			FilesUtils.createNewFile(file);
			createCommandDataFile();
			writer = TextFileUtils.getWriter(file, false);
			log.info("running command " + commandData.command() + " with concurrency " + commandData.concurrency());
			writeLine("running command " + commandData.command() + " with concurrency " + commandData.concurrency());
			writeLine("running command on " + commandData.nodes().size() + " nodes");
			if (commandData.nodes().size() < 11) {
				Function<NodeDataJson, String> predicate = new Function<NodeDataJson, String>(){
					@Override
					public String apply(NodeDataJson input) {
						return input.node_alias();
					}
				};
				writeLine("nodes list: " + StringUtils.collectionToString(commandData.nodes(), predicate));
			}
			new Thread() {
				@Override
				public void run() {
					execute();
				};
			}.start();
			return dirName;
		} catch (Exception ex) {
			finish();
			throw ExceptionUtils.asUnchecked(ex);
		}
	}

	private void finish() {
		commandDataJson.finish();
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
			if (commandData.commandExcutionType() == CommandExcutionType.Imediatley){
				strategy = new ImmediatlyCommandStrategy(this, commandData, links);
			}
			else { 
				strategy = new ProgressiveExecutionStrategy(this, commandData, links, nodeGetter);
			}
			strategy.execute();
			if (strategy.isCancel()) {
				writeLine("command was canceled by user");
			}
			writeLine("finished!");
			writeLine("failed nodes: " + commandDataJson.fail_list());
			writeLine("=========> aggregate-command-statistics (fail/total): " + fail + "/" + total + "\n");
		} finally {
			finish();
		}
	}

	private void createCommandDataFile() {
		commandDataJson = new CommandDataJson(commandData.command(), commandData.params(), commandData.project_name(),
				commandData.nodes(), dirName);
		FilesUtils.createNewFile(commandFile());
		updateJson();
	}

	private String commandFile() {
		return dirNameFull + Constants.JSON_COMMAND_FILE_NAME;
	}

	private long getNewDirName() {
		long i = 0;
		String dir = pathHelper.getPluginsOutputDir(commandData.project_name());
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

	public void fail(NodeDataJson node) {
		fail++;
		synchronized (commandDataJson) {
			commandDataJson.addFailedNode(node);
		}
	}

	public void nodeSuccess(NodeDataJson node) {
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
		return commandData.command();
	}

	public int success() {
		return (int) (count - fail) * 100 / total;
	}

	public int error() {
		return fail * 100 / total;
	}

	public String project() {
		return commandData.project_name();
	}

	public int nodes() {
		return commandData.nodes().size();
	}

	public CommandDataJson commandData() {
		return commandDataJson;
	}

	public long id() {
		return dirName;
	}

	public void cancel() {
		strategy.setCancel();
	}
}