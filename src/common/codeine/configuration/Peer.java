package codeine.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

public class Peer
{
	public List<Node> node = new ArrayList<Node>();
	public String dns_name;
	public String name;
	public List<String> mailingList = new ArrayList<String>();
	private transient InternalNode internalNode = new InternalNode(this);
	
	public Peer()
	{
		
	}
	
	public String dnsName()
	{
	    return null == dns_name ? name : dns_name;
	}

	public List<String> mailingList()
	{
		return mailingList;
	}

	@Override
	public String toString() {
	    return "Peer [node=" + node + ", dns_name=" + dns_name + ", name="
		    + name + ", mailingList=" + mailingList + "]";
	}

	public Node internalNode()
	{
		return internalNode;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dns_name == null) ? 0 : dns_name.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Peer other = (Peer)obj;
		if (dns_name == null)
		{
			if (other.dns_name != null)
				return false;
		}
		else if (!dns_name.equals(other.dns_name))
			return false;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}

	public Collection<Node> node()
	{
		if (node.isEmpty())
		{
			//default node
			return Lists.newArrayList(new Node(name, null, this));
		}
		return node;
	}

}
