package yami.configuration;

import static com.google.common.collect.Lists.*;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.google.common.collect.Lists;

public class Project
{
	@XmlAttribute
	public String name = "project_name";
	@XmlElement(name = "peer")
	public List<Peer> peers = Lists.newArrayList();
	@XmlElement(name = "collector")
	public List<HttpCollector> collectors = Lists.newArrayList();
	public List<String> mailingList = Lists.newArrayList();
	public List<MailPolicy> mailingPolicy = newArrayList();
	public List<Command> command = Lists.newArrayList();
	
	public List<Node> appInstances()
	{
		List<Node> $ = new ArrayList<Node>();
		for (Peer p : peers)
		{
			$.addAll(p.nodes);
		}
		return $;
	}
	
	public void afterUnmarshal(Unmarshaller u, Object parent) 
	{
		collectors.add(new VersionCollector());
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
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((collectors == null) ? 0 : collectors.hashCode());
		result = prime * result + ((mailingList == null) ? 0 : mailingList.hashCode());
		result = prime * result + ((mailingPolicy == null) ? 0 : mailingPolicy.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
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
}
