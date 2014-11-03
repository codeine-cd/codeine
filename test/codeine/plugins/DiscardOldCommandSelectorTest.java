package codeine.plugins;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import codeine.jsons.CommandExecutionStatusInfo;
import codeine.jsons.project.DiscardOldCommandsJson;

import com.google.common.collect.Lists;

public class DiscardOldCommandSelectorTest {

	private List<CommandExecutionStatusInfo> allsCommands = Lists.newArrayList();
	
	@Test
	public void testDisabled() {
		DiscardOldCommandsJson discardOldCommandsJson = new DiscardOldCommandsJson();
		DiscardOldCommandSelector tested = new DiscardOldCommandSelector(discardOldCommandsJson, allsCommands, TimeUnit.DAYS.toMillis(2));
		allsCommands.add(new CommandExecutionStatusInfo());
		assertEquals(Lists.newArrayList(), tested.commandsToDelete());
	}
	@Test
	public void testTimePassed() {
		DiscardOldCommandsJson discardOldCommandsJson = new DiscardOldCommandsJson(100, 1);
		DiscardOldCommandSelector tested = new DiscardOldCommandSelector(discardOldCommandsJson, allsCommands, TimeUnit.DAYS.toMillis(2));
		allsCommands.add(createCommand(TimeUnit.DAYS.toMillis(1)));
		CommandExecutionStatusInfo createCommand = createCommand(0);
		allsCommands.add(createCommand);
		assertEquals(Lists.newArrayList(createCommand), tested.commandsToDelete());
	}
	private CommandExecutionStatusInfo createCommand(long finish_time) {
		CommandExecutionStatusInfo commandExecutionStatusInfo2 = new CommandExecutionStatusInfo();
		commandExecutionStatusInfo2.finish_time(finish_time);
		return commandExecutionStatusInfo2;
	}
	@Test
	public void testNoFinishTimeUsingStartTime() {
		CommandExecutionStatusInfo commandExecutionStatusInfo2 = new CommandExecutionStatusInfo();
		commandExecutionStatusInfo2.start_time(1);
		assertEquals(1, commandExecutionStatusInfo2.finishTimeForRemoval());
	}
	@Test
	public void testMaxPassed() {
		DiscardOldCommandsJson discardOldCommandsJson = new DiscardOldCommandsJson(1, 100);
		DiscardOldCommandSelector tested = new DiscardOldCommandSelector(discardOldCommandsJson, allsCommands, TimeUnit.DAYS.toMillis(2));
		CommandExecutionStatusInfo createCommand1 = createCommand(1);
		allsCommands.add(createCommand1);
		CommandExecutionStatusInfo createCommand2 = createCommand(2);
		allsCommands.add(createCommand2);
		CommandExecutionStatusInfo createCommand0 = createCommand(0);
		allsCommands.add(createCommand0);
		assertEquals(Lists.newArrayList(createCommand0, createCommand1), tested.commandsToDelete());
	}

}
