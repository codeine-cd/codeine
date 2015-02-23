package codeine.statistics;

import java.util.List;

import codeine.api.CommandExecutionStatusInfo;
import codeine.executer.Task;

public interface IMonitorStatistics extends Task {

	List<MonitorStatusItem> getData(String projectName);
	void updateCommand(CommandExecutionStatusInfo command);
}