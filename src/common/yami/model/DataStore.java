package yami.model;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yami.configuration.ConfigurationManager;
import yami.configuration.HttpCollector;
import yami.configuration.Node;
import yami.configuration.Peer;
import yami.configuration.Project;
import yami.mail.CollectorOnNodeState;

import com.google.inject.Inject;

public class DataStore implements IDataStore
{
	public Map<Node, Map<HttpCollector, CollectorOnNodeState>> resultsByNode = new HashMap<Node, Map<HttpCollector, CollectorOnNodeState>>();
	private Map<Peer, Long> peersToSilentPeriod = newHashMap();
	
	private final ConfigurationManager configurationManager;

	@Inject
	public DataStore(ConfigurationManager configurationManager)
	{
		super();
		this.configurationManager = configurationManager;
	}
	
	
	public void addResults(Node node, HttpCollector collector, Result r)
	{
		Map<HttpCollector, CollectorOnNodeState> map = resultsByNode.get(node);
		if (null == map)
		{
			map = new HashMap<HttpCollector, CollectorOnNodeState>();
			resultsByNode.put(node, map);
		}
		CollectorOnNodeState c = map.get(collector);
		if (null == c)
		{
			c = new CollectorOnNodeState();
			map.put(collector, c);
		}
		c.addResult(r);
	}
	
	public CollectorOnNodeState getResult(Node node, HttpCollector collector)
	{
		Map<HttpCollector, CollectorOnNodeState> map = resultsByNode.get(node);
		if (null == map)
		{
			return null;
		}
		else
		{
			return map.get(collector);
		}
	}
	
	public boolean ok()
	{
		for (Node node : configuration().appInstances())
		{
			for (HttpCollector collector : configuration().collectors)
			{
				CollectorOnNodeState result = getResult(node, collector);
				if (null == result || !result.state())
				{
					return false;
				}
			}
		}
		return true;
	}

	private Project configuration() {
		return configurationManager.getConfiguredProject();
	}

	public List<Node> enabledInternalNodes()
	{
		List<Node> internalNodes = configuration().internalNodes();
		List<Node> $ = newArrayList();
		for (Node node : internalNodes)
		{
			Long silent = peersToSilentPeriod.get(node.peer);
			if (silent == null || silent < System.currentTimeMillis())
			{
				$.add(node);
			}
		}
		return $;
	}
	
	public void addSilentPeriod(Peer peer, long until)
	{
		peersToSilentPeriod.put(peer, until);
	}

}


