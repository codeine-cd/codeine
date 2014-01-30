package codeine.statistics;

import codeine.executer.Task;

public interface IMonitorStatistics extends Task {

	String getDataJson(String projectName);

}