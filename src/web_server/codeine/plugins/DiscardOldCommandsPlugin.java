package codeine.plugins;

import java.io.File;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.log4j.Logger;

import codeine.configuration.PathHelper;
import codeine.executer.ThreadPoolUtils;
import codeine.jsons.CommandExecutionStatusInfo;
import codeine.jsons.project.ProjectJson;
import codeine.utils.FilesUtils;
import codeine.utils.TextFileUtils;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.inject.Inject;

public class DiscardOldCommandsPlugin {

	private static final Logger log = Logger.getLogger(DiscardOldCommandsPlugin.class);

	
	@Inject	private PathHelper pathHelper;
	@Inject	private Gson gson;
	private ThreadPoolExecutor executor = ThreadPoolUtils.newThreadPool(2);

	public void queueForDelete(final ProjectJson project) {
		if (!project.discard_old_commands().enabled()) {
			return;
		}
		Runnable commandsDeleteRunner = new Runnable() {
			@Override
			public void run() {
				List<File> commands = pathHelper.getCommands(project.name());
				List<CommandExecutionStatusInfo> allsCommands = filesToCommands(project.name(), commands);
				List<CommandExecutionStatusInfo> commandsToDelete = new DiscardOldCommandSelector(project.discard_old_commands(), allsCommands, System.currentTimeMillis()).commandsToDelete();
				deleteCommands(commandsToDelete);
			}
		};
		executor.execute(commandsDeleteRunner );
	}
	
	private void deleteCommands(List<CommandExecutionStatusInfo> commandsToDelete) {
		for (CommandExecutionStatusInfo commandExecutionStatusInfo : commandsToDelete) {
			String commandOutputDir = pathHelper.getCommandOutputDir(commandExecutionStatusInfo.project_name(), String.valueOf(commandExecutionStatusInfo.id()));
			log.info("deleteCommands - deleting " + commandExecutionStatusInfo);
			try {
				FilesUtils.delete(commandOutputDir);
			} catch (Exception e) {
				log.warn("failed to delete " + commandOutputDir);
			}
		}
	}
	
	private List<CommandExecutionStatusInfo> filesToCommands(String projectName, List<File> commands) {
		List<CommandExecutionStatusInfo> $ = Lists.newArrayList();
		for (File command : commands) {
			String commandOutputInfoFile = pathHelper.getCommandOutputInfoFile(projectName, command.getName());
			try {
				String json = TextFileUtils.getContents(commandOutputInfoFile);
				$.add(gson.fromJson(json, CommandExecutionStatusInfo.class));
			} catch (Exception e) {
				log.warn("failed to read " + commandOutputInfoFile);
			}
		}
		return $;
	}
	
}
