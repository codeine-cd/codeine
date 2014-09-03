package codeine.servlet;

import java.util.Set;

@SuppressWarnings("unused")
public class ManageStatisticsInfo {
	
	private Set<String> users;
	private Set<String> urls;

	public ManageStatisticsInfo(Set<String> users, Set<String> urls) {
		this.users = users;
		this.urls = urls;
	}

}
