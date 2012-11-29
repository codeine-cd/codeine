package yami.configuration;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import yami.model.DataStore;

public class Nodes
{

	private static final Logger log = Logger.getLogger(Nodes.class);

	public static List<Node> getNodes(String hostname, DataStore dataStore) throws Exception
	{
		boolean found = false;
		List<Node> nodes = new ArrayList<Node>();
		for (Node node : dataStore.appInstances())
		{
			if (!hostname.equals(node.node.name))
			{
				continue;
			}
			found = true;
			nodes.add(node);
		}
		if (!found)
		{
			log.warn("Client " + hostname + " has no monitoring nodes configured");
			throw new Exception("Client is not configured to run on " + hostname);
		}
		return nodes;
	}
	
}
