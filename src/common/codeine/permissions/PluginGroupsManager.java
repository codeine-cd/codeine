package codeine.permissions;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.executer.ThreadPoolUtils;
import codeine.jsons.global.ExperimentalConfJsonStore;
import codeine.utils.ExceptionUtils;
import codeine.utils.os_process.ProcessExecuter;

import com.google.common.base.Splitter;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

public class PluginGroupsManager extends GroupsManager{

	private static final Logger log = Logger.getLogger(PluginGroupsManager.class);
	private static final String $USER = "$user";
	private static final int MAX_USERS = 1000;
	@Inject
	private ExperimentalConfJsonStore experimentalConfJsonStore;

	private LoadingCache<String, List<String>> groups = CacheBuilder.newBuilder().maximumSize(MAX_USERS)
			.refreshAfterWrite(20, TimeUnit.MINUTES)
			.build(CacheLoader.asyncReloading(new CacheLoader<String, List<String>>() {
				@Override
				public List<String> load(String user) {
					if (null == experimentalConfJsonStore.get().groups_plugin()) {
						return Lists.newArrayList();
					}
					List<String> $ = Splitter.on(",").omitEmptyStrings().splitToList(
							ProcessExecuter.executeSuccess(
									experimentalConfJsonStore.get().groups_plugin().replace($USER, user)));
					log.info("resolved groups for user " + user + " : " + $);
					return $;
				}
			}, ThreadPoolUtils.newFixedThreadPool(2, "PluginGroupsManager")));

	@Override
	public List<String> groups(String user) {
		try {
			return groups.get(user);
		} catch (ExecutionException e) {
			throw ExceptionUtils.asUnchecked(e);
		}
	}
}
