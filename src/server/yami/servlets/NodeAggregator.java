package yami.servlets;

import static com.google.common.collect.Maps.newHashMap;

import java.util.List;
import java.util.Map;

import yami.configuration.Node;
import yami.model.IDataStore;
import yami.model.VersionResult;

public class NodeAggregator
{

	public Map<String, VersionItem> aggregate(List<Node> nodes, IDataStore dataStore)
	{
		Map<String, VersionItem> items = newHashMap();
		for (Node node : nodes)
		{
			String version = VersionResult.getVersion(dataStore, node);
			VersionItem versionItem = items.get(version);
			if (null == versionItem)
			{
				versionItem = new VersionItem(version);
				items.put(version, versionItem);
			}
			versionItem.add(node);
		}
		return items;
	}

}
