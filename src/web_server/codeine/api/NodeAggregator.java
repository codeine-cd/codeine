package codeine.api;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import codeine.executer.ThreadPoolUtils;
import codeine.jsons.labels.LabelJsonProvider;
import codeine.model.Constants;

import com.google.common.base.Stopwatch;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;


public class NodeAggregator
{
	private static final Logger log = Logger.getLogger(NodeAggregator.class);
	@Inject	private LabelJsonProvider versionLabelJsonProvider;
	@Inject private NodeGetter nodesGetter;
	
	private LoadingCache<String, Integer> nodesCount = CacheBuilder.newBuilder()
			.refreshAfterWrite(1, TimeUnit.MINUTES)
			.build(CacheLoader.asyncReloading(new CacheLoader<String, Integer>() {
				@Override
				public Integer load(String project) {
					Stopwatch s = Stopwatch.createStarted();
					VersionItemInfo versionItem = aggregateInternal(project).get(Constants.ALL_VERSION);
					int $ = versionItem.count();
					log.info("count for project " + project + " is " + $ + " took " + s);
					return $;
				}
			}, ThreadPoolUtils.newFixedThreadPool(1, "NodeAggregatorCount")));
	
	
	public Map<String, VersionItemInfo> aggregate(String projectName) {
		Map<String, VersionItemInfo> $ = aggregateInternal(projectName);
		nodesCount.put(projectName, $.get(Constants.ALL_VERSION).count());
		return $;
	}

	private Map<String, VersionItemInfo> aggregateInternal(String projectName) {
		Multimap<String, NodeWithMonitorsInfo> items = ArrayListMultimap.create();
		List<NodeWithMonitorsInfo> nodes = nodesGetter.getNodes(projectName);
		for (NodeWithMonitorsInfo nodeInfo : nodes) {
			String version = nodeInfo.version();
			String versionLabel = versionLabelJsonProvider.labelForVersion(version, projectName);
			items.put(Constants.ALL_VERSION, nodeInfo);
			items.put(versionLabel, nodeInfo);
		}
		
		Map<String, VersionItemInfo> $ = newHashMap();
		int max = getMax(items);
		for (Entry<String, Collection<NodeWithMonitorsInfo>> e : items.asMap().entrySet()) {
			int countBad = percent(false, e.getValue(), max);
			String versionName = versionLabelJsonProvider.versionForLabel(e.getKey(), projectName);
			$.put(e.getKey(), new VersionItemInfo(e.getKey(), versionName, countBad, e.getValue().size(), max));
		}
		if ($.isEmpty()){
			$.put(Constants.ALL_VERSION, new VersionItemInfo(Constants.ALL_VERSION, Constants.ALL_VERSION, 0,0,0));
		}
		return $;
	}

	private int getMax(Multimap<String, NodeWithMonitorsInfo> items) {
		int max = 0;
		for (Entry<String, Collection<NodeWithMonitorsInfo>> e : items.asMap().entrySet()) {
			if (!e.getKey().equals(Constants.ALL_VERSION)){
				max = Math.max(max, e.getValue().size());
			}
		}
		return max;
	}

	private int percent(boolean b, Collection<NodeWithMonitorsInfo> collection, int total) {
		int count = 0;
		for (NodeWithMonitorsInfo entry : collection) {
			if (entry.status() == b){
				count++;
			}
		}
		return count;
	}

	public int count(String name) {
		try {
			Integer $ = nodesCount.get(name);
			if ($ != null) {
				return $;
			}
		} catch (Exception e) {
			log.warn("got exception", e);
		}
		return 0;
	}

}
