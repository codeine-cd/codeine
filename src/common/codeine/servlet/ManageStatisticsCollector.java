package codeine.servlet;

import codeine.permissions.IUserWithPermissions;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class ManageStatisticsCollector {

	Cache<String, String> usersInfo = CacheBuilder.newBuilder().maximumSize(20).build();
	Cache<String, String> urlsInfo = CacheBuilder.newBuilder().maximumSize(20).build();
	
	public ManageStatisticsInfo getCollected() {
		return new ManageStatisticsInfo(usersInfo.asMap().keySet(), urlsInfo.asMap().keySet());
	}
	public void userAccess(IUserWithPermissions user, String pathInfo) {
		usersInfo.put(user.user().username(), user.user().username());
		urlsInfo.put(pathInfo, pathInfo);
	}

}
