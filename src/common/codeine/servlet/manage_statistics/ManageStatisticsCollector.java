package codeine.servlet.manage_statistics;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import codeine.permissions.IUserWithPermissions;
import codeine.utils.ExceptionUtils;
import codeine.utils.network.UserAgentHeader;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;

public class ManageStatisticsCollector {

	private Cache<String, StringWithCount> usersInfo = CacheBuilder.newBuilder().maximumSize(200).build();
	private Cache<String, String> activeUsersInfo = CacheBuilder.newBuilder().maximumSize(200).expireAfterWrite(20, TimeUnit.MINUTES).build();
	private Cache<String, StringsCommandPair> lastCommandsInfo = CacheBuilder.newBuilder().maximumSize(20).build();
	
	public synchronized ManageStatisticsInfo getCollected() {
		List<StringsCommandPair> lastCommands = Lists.newArrayList(lastCommandsInfo.asMap().values());
		List<StringWithCount> users = Lists.newArrayList(usersInfo.asMap().values());
		Collections.sort(users, new Comparator<ManageStatisticsCollector.StringWithCount>() {
			@Override
			public int compare(StringWithCount o1, StringWithCount o2) {
				return o1.hitCount == o2.hitCount ? o1.value.compareTo(o2.value) : Integer.compare(o2.hitCount, o1.hitCount);
			}
		});
		Collections.sort(lastCommands, new StringsCommandPair.CommandComparator());
		return new ManageStatisticsInfo(users, lastCommands, activeUsersInfo.asMap().keySet());
	}
	public void userAccess(IUserWithPermissions user, final String pathInfo, HttpServletRequest request) {
		String username = user.user().username();
		activeUsersInfo.put(username, username);
		try {
			UserAgentHeader userAgent = UserAgentHeader.parseBrowserAndOs(request);
			String userWithAgent = username + "  ___  browser=" + userAgent.getBrowser() + ",os=" + userAgent.getOs();
			incHitCount(userWithAgent);
		} catch (ExecutionException e) {
			throw ExceptionUtils.asUnchecked(e);
		}
	}
	private synchronized void incHitCount(String userWithAgent) throws ExecutionException {
		usersInfo.get(userWithAgent , getCallable(userWithAgent)).hitCount++;
	}
	public synchronized void commandExecuted(String project, String command_name, String command_id, long startTime) {
		lastCommandsInfo.put(project + "_" + command_name + "_" + command_id, new StringsCommandPair(project, command_name, command_id, startTime));
	}
	private Callable<StringWithCount> getCallable(final String pathInfo) {
		Callable<StringWithCount> callable = new Callable<ManageStatisticsCollector.StringWithCount>() {
			@Override
			public StringWithCount call() throws Exception {
				return new StringWithCount(pathInfo);
			}
		};
		return callable;
	}
	
	public static class StringWithCount {
		public StringWithCount(String pathInfo) {
			value = pathInfo;
		}
		private String value;
		private int hitCount;
	}

}
