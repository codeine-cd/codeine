package codeine.configuration;

import java.util.List;

import com.google.common.collect.Lists;

public class CollectorRule
{
	private String node;
	private String peer;
	private List<String> arg = Lists.newArrayList();
	
	

	public CollectorRule(){
		
	}
	
	public CollectorRule(String peer, String node, List<String> arg) {
		super();
		this.peer = peer;
		this.node = node;
		this.arg = arg;
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

	@Override
	public String toString()
	{
		return "CollectorRule [node=" + node + ", peer=" + peer + ", arg=" + arg + "]";
	}

	public List<String> arg() {
		return arg;
	}
	
	
	
}
