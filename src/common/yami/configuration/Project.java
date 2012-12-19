package yami.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class Project
{
	@XmlElement(name = "peer")
	public List<Peer> peers = new ArrayList<Peer>();
	@XmlElement(name = "collector")
	public List<HttpCollector> collectors = new ArrayList<HttpCollector>();
	public List<String> mailingList = new ArrayList<String>();
	public List<MailPolicy> mailingPolicy = new ArrayList<MailPolicy>();
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((collectors == null) ? 0 : collectors.hashCode());
		result = prime * result + ((mailingList == null) ? 0 : mailingList.hashCode());
		result = prime * result + ((mailingPolicy == null) ? 0 : mailingPolicy.hashCode());
		result = prime * result + ((peers == null) ? 0 : peers.hashCode());
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
		Project other = (Project)obj;
		if (collectors == null)
		{
			if (other.collectors != null)
				return false;
		}
		else if (!collectors.equals(other.collectors))
			return false;
		if (mailingList == null)
		{
			if (other.mailingList != null)
				return false;
		}
		else if (!mailingList.equals(other.mailingList))
			return false;
		if (mailingPolicy == null)
		{
			if (other.mailingPolicy != null)
				return false;
		}
		else if (!mailingPolicy.equals(other.mailingPolicy))
			return false;
		if (peers == null)
		{
			if (other.peers != null)
				return false;
		}
		else if (!peers.equals(other.peers))
			return false;
		return true;
	}
	
	public List<Node> appInstances()
	{
		List<Node> $ = new ArrayList<Node>();
		for (Peer p : peers)
		{
			$.addAll(p.nodes);
		}
		return $;
	}
	
	public HttpCollector getCollector(String name)
	{
		for (HttpCollector c : collectors)
		{
			if (c.name.equals(name))
			{
				return c;
			}
		}
		return null;
	}
	
}
