package codeine.servlet;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import codeine.permissions.IUserWithPermissions;
import codeine.utils.ExceptionUtils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;

public class ManageStatisticsCollector {

	Cache<String, String> usersInfo = CacheBuilder.newBuilder().maximumSize(20).build();
	Cache<String, UrlInfo> urlsInfo = CacheBuilder.newBuilder().maximumSize(20).build();
	
	public synchronized ManageStatisticsInfo getCollected() {
		List<UrlInfo> urls = Lists.newArrayList(urlsInfo.asMap().values());
		Comparator<UrlInfo> c = new Comparator<ManageStatisticsCollector.UrlInfo>() {
			@Override
			public int compare(UrlInfo o1, UrlInfo o2) {
				return o1.hitCount == o2.hitCount ? o1.path.compareTo(o2.path) : Integer.compare(o1.hitCount, o2.hitCount);
			}
		};
		Collections.sort(urls, c);
		return new ManageStatisticsInfo(usersInfo.asMap().keySet(), urls);
	}
	public synchronized void userAccess(IUserWithPermissions user, final String pathInfo) {
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
	
	public static class UrlInfo {
		public UrlInfo(String pathInfo) {
			path = pathInfo;
		}
		private String path;
		private int hitCount;
	}

}
