package codeine.statistics;

import java.util.List;

import codeine.executer.Task;
import codeine.jsons.CommandExecutionStatusInfo;

public interface IMonitorStatistics extends Task {

	List<MonitorStatusItem> getData(String projectName);
	void updateCommand(CommandExecutionStatusInfo command);
}