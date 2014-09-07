package codeine.servlet;

import java.util.Collection;
import java.util.Set;

import codeine.servlet.ManageStatisticsCollector.UrlInfo;

@SuppressWarnings("unused")
public class ManageStatisticsInfo {
	
	private Set<String> users;
	private Collection<UrlInfo> urls;
	private Set<String> activeUsers;

	public ManageStatisticsInfo(Set<String> users, Collection<UrlInfo> urls, Set<String> activeUsers) {
		this.users = users;
		this.urls = urls;
		this.activeUsers = activeUsers;
	}

}
