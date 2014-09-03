package codeine.servlet;

import java.util.Collection;
import java.util.Set;

import codeine.servlet.ManageStatisticsCollector.UrlInfo;

@SuppressWarnings("unused")
public class ManageStatisticsInfo {
	
	private Set<String> users;
	private Collection<UrlInfo> urls;

	public ManageStatisticsInfo(Set<String> users, Collection<UrlInfo> urls) {
		this.users = users;
		this.urls = urls;
	}

}
