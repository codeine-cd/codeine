package codeine.plugins;

import java.io.File;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.log4j.Logger;

import codeine.api.CommandExecutionStatusInfo;
import codeine.configuration.PathHelper;
import codeine.executer.ThreadPoolUtils;
import codeine.jsons.project.ProjectJson;
import codeine.utils.FilesUtils;
import codeine.utils.JsonUtils;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class DiscardOldCommandsPlugin {

	private static final Logger log = Logger.getLogger(DiscardOldCommandsPlugin.class);

	
	@Inject	private PathHelper pathHelper;
	private ThreadPoolExecutor executor = ThreadPoolUtils.newThreadPool(2, "DiscardOldCommandsPlugin");

	public void queueForDelete(final ProjectJson project) {
		if (!project.discard_old_commands().enabled()) {
			log.info("discard_old_commands not enabled for project " + project.name());
			return;
		}
		Runnable commandsDeleteRunner = new Runnable() {
			@Override
			public void run() {
				log.info("discard_old_commands starting for project " + project.name());
				List<File> commands = pathHelper.getCommandsOutput(project.name());
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
				$.add(JsonUtils.fromJsonFromFile(commandOutputInfoFile, CommandExecutionStatusInfo.class));
			} catch (Exception e) {
				log.warn("failed to read " + commandOutputInfoFile);
			}
		}
		return $;
	}
	
}
