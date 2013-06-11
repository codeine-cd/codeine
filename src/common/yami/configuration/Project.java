package yami.configuration;

import static com.google.common.collect.Lists.newArrayList;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;

public class Project implements IConfigurationObject
{
	private static final Logger log = Logger.getLogger(Project.class);
	@XmlAttribute
	public String name;
	@XmlElement(name = "peer")
	public List<Peer> peers = Lists.newArrayList();
	@XmlElement(name = "collector")
	public List<HttpCollector> collectors = Lists.newArrayList();
	public List<String> mailingList = Lists.newArrayList();
	public List<MailPolicy> mailingPolicy = newArrayList();
	public List<Command> command = Lists.newArrayList();
	private IConfigurationObject parent;
	
	public List<Node> appInstances()
	{
		List<Node> $ = new ArrayList<Node>();
		for (Peer p : peers)
		{
			$.addAll(p.node());
		}
		return $;
	}
	
	public HttpCollector getCollector(String name1)
	{
		for (HttpCollector c : collectors)
		{
			if (c.name.equals(name1))
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

	@Override
	public Yami getConfiguration() {
		return parent.getConfiguration();
	}
	
	public void afterUnmarshal(Unmarshaller u, Object parent)
	{
		this.parent = (IConfigurationObject)parent;
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
	
	public List<Node> nodes()
	{
		List<Node> $ = newArrayList();
		List<Peer> peers = peers();
		for (Peer peer : peers)
		{
			$.addAll(peer.node());
		}
		return $;
	}

	public List<Peer> peers() {
		return peers;
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
		return this;
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
	
	public List<Node> getNodes(String hostname)
	{
		boolean found = false;
		List<Node> nodes = new ArrayList<Node>();
		for (Node node : appInstances())
		{
			if (!hostname.equals(node.peer.name))
			{
				continue;
			}
			found = true;
			nodes.add(node);
		}
		if (!found)
		{
			log.warn("Peer " + hostname + " has no monitoring nodes configured");
			throw new RuntimeException("Peer is not configured to run on " + hostname);
		}
		return nodes;
	}
}
