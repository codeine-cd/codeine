package codeine.servlet;

import java.util.List;
import java.util.Set;

import codeine.servlet.ManageStatisticsCollector.StringWithCount;
import codeine.servlet.ManageStatisticsCollector.StringsCommandPair;

@SuppressWarnings("unused")
public class ManageStatisticsInfo {
	
	private List<StringWithCount> users;
	private List<StringsCommandPair> lastCommands;
	private Set<String> activeUsers;

	public ManageStatisticsInfo(List<StringWithCount> users, List<StringsCommandPair> lastCommands, Set<String> activeUsers) {
		this.users = users;
		this.lastCommands = lastCommands;
		this.activeUsers = activeUsers;
	}

}
