package yami.configuration;

import java.util.*;

public class CollectorRule
{
	public String node = null;
	public String peer = null;
	public List<String> arg = new ArrayList<String>();
	
	public CollectorRule()
	{
	}
	
	public boolean shouldApplyForNode(String name)
	{
		if (null == node || node.equals("all") || name.matches(node))
		{
			return true;
		}
		return false;
	}
	
	public boolean shouldApplyForNode(Node node)
	{
		return shouldApplyForNode(node.name);
	}
	
	public boolean shouldApplyForPeer(Peer peer)
	{
		return shouldApplyForNode(peer.name);
	}
	
	public boolean shouldApplyForPeer(String peer)
	{
		return shouldApplyForNode(peer);
	}
}


