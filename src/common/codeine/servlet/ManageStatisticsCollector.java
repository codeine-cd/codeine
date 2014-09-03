package codeine.servlet;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import codeine.permissions.IUserWithPermissions;
import codeine.utils.ExceptionUtils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class ManageStatisticsCollector {

	Cache<String, String> usersInfo = CacheBuilder.newBuilder().maximumSize(20).build();
	Cache<String, UrlInfo> urlsInfo = CacheBuilder.newBuilder().maximumSize(20).build();
	
	public ManageStatisticsInfo getCollected() {
		return new ManageStatisticsInfo(usersInfo.asMap().keySet(), urlsInfo.asMap().values());
	}
	public void userAccess(IUserWithPermissions user, final String pathInfo) {
		usersInfo.put(user.user().username(), user.user().username());
		Callable<UrlInfo> callable = new Callable<ManageStatisticsCollector.UrlInfo>() {
			@Override
			public UrlInfo call() throws Exception {
				return new UrlInfo(pathInfo);
			}
		};
		try {
			UrlInfo urlInfo = urlsInfo.get(pathInfo, callable);
			urlInfo.hitCount++;
		} catch (ExecutionException e) {
			throw ExceptionUtils.asUnchecked(e);
		}
	}
	
	@SuppressWarnings("unused")
	public static class UrlInfo {
		public UrlInfo(String pathInfo) {
			path = pathInfo;
		}
		private String path;
		private int hitCount;
	}

}
