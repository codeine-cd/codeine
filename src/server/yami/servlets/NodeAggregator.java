package yami.servlets;

import static com.google.common.collect.Maps.*;

import java.util.List;
import java.util.Map;

import yami.configuration.Node;
import yami.configuration.VersionCollector;
import yami.mail.CollectorOnNodeState;
import yami.model.DataStore;
import yami.model.DataStoreRetriever;
import yami.model.Result;

public class NodeAggregator
{

	private static final String NO_VERSION = "No version";

	public Map<String, VersionItem> aggregate()
	{
		DataStore d = DataStoreRetriever.getD();
		Map<String, VersionItem> items = newHashMap();
		List<Node> nodes = d.nodes();
		for (Node node : nodes)
		{
			VersionCollector c = new VersionCollector();
			String version = getVersion(d, node, c);
			if (null == version || version.isEmpty())
			{
				version = NO_VERSION;
			}
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

	private String getVersion(DataStore d, Node node, VersionCollector c)
	{
		CollectorOnNodeState result = d.getResult(node, c);
		if (null == result){
			return NO_VERSION;
		}
		Result last = result.getLast();
		if (null == last) {
			return NO_VERSION;
		}
		String[] split = last.output.split("\n");
		return split[split.length-1];
	}
	
}
