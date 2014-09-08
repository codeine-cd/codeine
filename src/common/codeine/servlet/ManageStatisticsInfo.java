package codeine.servlet;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import codeine.servlet.ManageStatisticsCollector.StringWithCount;

@SuppressWarnings("unused")
public class ManageStatisticsInfo {
	
	private List<StringWithCount> users;
	private Collection<StringWithCount> urls;
	private Set<String> activeUsers;

	public ManageStatisticsInfo(List<StringWithCount> users, Collection<StringWithCount> urls, Set<String> activeUsers) {
		this.users = users;
		this.urls = urls;
		this.activeUsers = activeUsers;
	}

}
