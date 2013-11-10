package codeine.servlets;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import codeine.api.NodeGetter;
import codeine.api.NodeWithMonitorsInfo;
import codeine.jsons.labels.LabelJsonProvider;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.version.VersionItem;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;


public class NodeAggregator
{
	@Inject	private LabelJsonProvider versionLabelJsonProvider;
	@Inject private NodeGetter nodesGetter;
	
	
	public Map<String, VersionItem> aggregate(ProjectJson projectJson) {
		Multimap<String, NodeWithMonitorsInfo> items = ArrayListMultimap.create();
		List<NodeWithMonitorsInfo> nodes = nodesGetter.getNodes(projectJson.name());
		
		for (NodeWithMonitorsInfo nodeInfo : nodes) {
			String version = nodeInfo.version();
			String versionLabel = versionLabelJsonProvider.labelForVersion(version, projectJson.name());
			items.put(Constants.ALL_VERSION, nodeInfo);
			items.put(versionLabel, nodeInfo);
		}
		
		Map<String, VersionItem> $ = newHashMap();
		int max = getMax(items);
		for (Entry<String, Collection<NodeWithMonitorsInfo>> e : items.asMap().entrySet()) {
			int percentBad = percent(false, e.getValue(), max);
			int percentGood = percent(true, e.getValue(), max);
			if (percentGood + percentBad > 100){
				percentGood = 100 - percentBad;
			}
			String versionName = versionLabelJsonProvider.versionForLabel(e.getKey(), projectJson.name());
			$.put(e.getKey(), new VersionItem(e.getKey(), versionName, percentBad, percentGood, e.getValue().size()));
		}
		if ($.isEmpty()){
			$.put(Constants.ALL_VERSION, new VersionItem(Constants.ALL_VERSION, Constants.ALL_VERSION, 0,0,0));
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
		return (int) Math.ceil(count * 100 / (double)total);
	}

}