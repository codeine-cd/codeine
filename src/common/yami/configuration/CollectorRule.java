package yami.configuration;

import java.util.List;

import com.google.common.collect.Lists;

public class CollectorRule
{
	public String node = "^$";
	public String peer = "^$";
	public List<MailPolicy> mailPolicy = null;
	public List<String> arg = Lists.newArrayList();
	
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
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((arg == null) ? 0 : arg.hashCode());
		result = prime * result + ((node == null) ? 0 : node.hashCode());
		result = prime * result + ((peer == null) ? 0 : peer.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CollectorRule other = (CollectorRule)obj;
		if (arg == null)
		{
			if (other.arg != null)
				return false;
		}
		else if (!arg.equals(other.arg))
			return false;
		if (node == null)
		{
			if (other.node != null)
				return false;
		}
		else if (!node.equals(other.node))
			return false;
		if (peer == null)
		{
			if (other.peer != null)
				return false;
		}
		else if (!peer.equals(other.peer))
			return false;
		return true;
	}
	
}
