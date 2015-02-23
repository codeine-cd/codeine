package codeine.plugins;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import codeine.api.CommandExecutionStatusInfo;
import codeine.jsons.project.DiscardOldCommandsJson;

import com.google.common.collect.Lists;

public class DiscardOldCommandSelector {

	private List<CommandExecutionStatusInfo> allsCommands;
	private DiscardOldCommandsJson discardOldCommandsJson;
	private long timeNow;

	public DiscardOldCommandSelector(DiscardOldCommandsJson discardOldCommandsJson, List<CommandExecutionStatusInfo> allsCommands, long timeNow) {
		this.discardOldCommandsJson = discardOldCommandsJson;
		this.allsCommands = allsCommands;
		this.timeNow = timeNow;
	}

	public List<CommandExecutionStatusInfo> commandsToDelete() {
		if (!discardOldCommandsJson.enabled()) {
			return Lists.newArrayList();
		}
		List<CommandExecutionStatusInfo> $ = Lists.newArrayList();
		if (null != discardOldCommandsJson.max_days()) {
			for (CommandExecutionStatusInfo commandExecutionStatusInfo : allsCommands) {
				long total_time = commandExecutionStatusInfo.finishTimeForRemoval() + TimeUnit.DAYS.toMillis(discardOldCommandsJson.max_days());
				if (total_time < timeNow) {
					$.add(commandExecutionStatusInfo);
				}
			}
		}
		List<CommandExecutionStatusInfo> whatsLeft = Lists.newArrayList(allsCommands);
		whatsLeft.removeAll($);
		if (null == discardOldCommandsJson.max_commands() || whatsLeft.size() <= discardOldCommandsJson.max_commands()) {
			return $;
		}
		for (Iterator<CommandExecutionStatusInfo> iterator = whatsLeft.iterator(); iterator.hasNext();) {
			CommandExecutionStatusInfo commandExecutionStatusInfo = iterator.next();
			if (null == commandExecutionStatusInfo) {
				iterator.remove();
			}
		}
		Collections.sort(whatsLeft, new Comparator<CommandExecutionStatusInfo>() {
			@Override
			public int compare(CommandExecutionStatusInfo arg0, CommandExecutionStatusInfo arg1) {
				return arg0.finishTimeForRemoval() == arg1.finishTimeForRemoval() ? 0 : arg0.finishTimeForRemoval() < arg1.finishTimeForRemoval() ? -1 : 1;
			}
		});
		$.addAll(whatsLeft.subList(0, whatsLeft.size() - discardOldCommandsJson.max_commands()));
		return $;
	}

}
