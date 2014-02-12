package codeine.statistics;

import java.util.List;

import codeine.executer.Task;

public interface IMonitorStatistics extends Task {

	List<MonitorStatusItem> getData(String projectName);

}