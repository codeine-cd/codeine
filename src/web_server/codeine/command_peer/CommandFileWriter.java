package codeine.command_peer;

import java.util.Set;

import org.apache.log4j.Logger;

import codeine.api.CommandExecutionStatusInfo;
import codeine.executer.Task;
import codeine.utils.TextFileUtils;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.inject.Inject;

public class CommandFileWriter implements Task{

	private final Logger log = Logger.getLogger(CommandFileWriter.class);
	
	@Inject	private Gson gson;
	private Set<CommandFileWriterItem> commandsSet = Sets.newConcurrentHashSet();

	public void queue(CommandFileWriterItem item) {
		commandsSet.add(item);
	}
	
	private void writeToFile(CommandFileWriterItem item) {
		String json;
		json = gson.toJson(item.commandExecutionInfo);
		synchronized (item.fileWriteSync) {
			TextFileUtils.setContents(item.commandFile, json);
		}
	}

	@Override
	public void run() {
		Set<CommandFileWriterItem> commandsSet2 = Sets.newHashSet(commandsSet);
		commandsSet.removeAll(commandsSet2);
		if (!commandsSet2.isEmpty()) {
			log.info("will write " + commandsSet2.size() + " command(s) to disk");
		}
		for (CommandFileWriterItem commandFileWriterItem : commandsSet2) {
			writeToFile(commandFileWriterItem);
		}
	}
	
	public static class CommandFileWriterItem {

		private Object fileWriteSync;
		private String commandFile;
		private CommandExecutionStatusInfo commandExecutionInfo;

		public CommandFileWriterItem(Object fileWriteSync, String commandFile,
				CommandExecutionStatusInfo commandExecutionInfo) {
					this.fileWriteSync = fileWriteSync;
					this.commandFile = commandFile;
					this.commandExecutionInfo = commandExecutionInfo;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((commandExecutionInfo == null) ? 0 : commandExecutionInfo.hashCode());
			result = prime * result + ((commandFile == null) ? 0 : commandFile.hashCode());
			result = prime * result + ((fileWriteSync == null) ? 0 : fileWriteSync.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CommandFileWriterItem other = (CommandFileWriterItem) obj;
			if (commandExecutionInfo == null) {
				if (other.commandExecutionInfo != null)
					return false;
			} else if (!commandExecutionInfo.equals(other.commandExecutionInfo))
				return false;
			if (commandFile == null) {
				if (other.commandFile != null)
					return false;
			} else if (!commandFile.equals(other.commandFile))
				return false;
			if (fileWriteSync == null) {
				if (other.fileWriteSync != null)
					return false;
			} else if (!fileWriteSync.equals(other.fileWriteSync))
				return false;
			return true;
		}
		
	}
}
