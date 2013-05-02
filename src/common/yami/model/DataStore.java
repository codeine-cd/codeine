package yami.model;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yami.configuration.HttpCollector;
import yami.configuration.MailPolicy;
import yami.configuration.Node;
import yami.configuration.Peer;
import yami.configuration.Project;
import yami.configuration.VersionCollector;
import yami.mail.CollectorOnNodeState;

public class DataStore implements IDataStore
{
	public Map<Node, Map<HttpCollector, CollectorOnNodeState>> resultsByNode = new HashMap<Node, Map<HttpCollector, CollectorOnNodeState>>();
	private Map<Peer, Long> peersToSilentPeriod = newHashMap();
	public DataStore()
	{
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
	
	public List<HttpCollector> collectors()
	{
		List<HttpCollector> $ = new ArrayList<HttpCollector>();
		$.addAll(configuration().collectors);
		return $;
	}
	
	public List<HttpCollector> implicitCollectors()
	{
		List<HttpCollector> $ = new ArrayList<HttpCollector>();
		$.add(new VersionCollector());
		return $;
	}
	public List<Peer> peers()
	{
		return configuration().peers;
	}
	
	public List<Node> appInstances()
	{
		return configuration().appInstances();
	}
	
	public List<MailPolicy> mailingPolicy()
	{
		return configuration().mailingPolicy;
	}
	
	public List<String> mailingList()
	{
		return configuration().mailingList;
	}
	
	private Project configuration()
	{
		return new DataStoreRetriever().readProject();
	}

	public List<Node> internalNodes()
	{
		List<Node> $ = newArrayList();
		for (Peer peer : peers())
		{
			$.add(peer.internalNode());
		}
		return $;
	}

	public List<Node> enabledInternalNodes()
	{
		List<Node> internalNodes = internalNodes();
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

	public Node getNodeByName(String nodeName)
	{
		List<Peer> peers = peers();
		for (Peer peer : peers)
		{
			for (Node node : peer.node())
			{
				if (nodeName.equals(node.name))
				{
					return node;
				}
			}
		}
		return null;
	}

}
