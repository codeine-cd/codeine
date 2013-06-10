package yami.servlets;

import static com.google.common.collect.Maps.*;

import java.util.List;
import java.util.Map;

import yami.configuration.Node;
import yami.model.DataStore;
import yami.model.DataStoreRetriever;
import yami.model.VersionResult;

public class NodeAggregator
{

	public Map<String, VersionItem> aggregate()
	{
		DataStore d = DataStoreRetriever.getD();
		Map<String, VersionItem> items = newHashMap();
		List<Node> nodes = d.nodes();
		for (Node node : nodes)
		{
			String version = VersionResult.getVersion(d, node);
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
