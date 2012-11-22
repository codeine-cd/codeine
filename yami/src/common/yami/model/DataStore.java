package yami.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yami.configuration.HttpCollector;
import yami.configuration.MailPolicy;
import yami.configuration.Node;
import yami.configuration.Peer;
import yami.configuration.Project;
import yami.mail.CollectorOnAppState;

public class DataStore implements IDataStore
{
	public Map<Node, Map<HttpCollector, CollectorOnAppState>> resultsByMonitoredApp = new HashMap<Node, Map<HttpCollector, CollectorOnAppState>>();
	
	public DataStore()
	{
	}
	
	public void addResults(Node node, HttpCollector collector, Result r)
	{
		Map<HttpCollector, CollectorOnAppState> map = resultsByMonitoredApp.get(node);
		if (null == map)
		{
			map = new HashMap<HttpCollector, CollectorOnAppState>();
			resultsByMonitoredApp.put(node, map);
		}
		CollectorOnAppState c = map.get(collector);
		if (null == c)
		{
			c = new CollectorOnAppState();
			map.put(collector, c);
		}
		c.addResult(r);
	}
	
	public CollectorOnAppState getResult(Node node, HttpCollector collector)
	{
		Map<HttpCollector, CollectorOnAppState> map = resultsByMonitoredApp.get(node);
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
			for (HttpCollector collector : configuration().collector)
			{
				CollectorOnAppState result = getResult(node, collector);
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
		$.addAll(configuration().collector);
		return $;
	}
	
	public List<Peer> nodes()
	{
		return configuration().peer;
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
}
