package yami.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Project
{
	public List<Peer> peer = new ArrayList<Peer>();
	public List<HttpCollector> collector = new ArrayList<HttpCollector>();
	public List<String> mailingList = new ArrayList<String>();
	public List<MailPolicy> mailingPolicy = new ArrayList<MailPolicy>();
	public GlobalConfiguration conf = new GlobalConfiguration();
	
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((collector == null) ? 0 : collector.hashCode());
		result = prime * result + ((mailingList == null) ? 0 : mailingList.hashCode());
		result = prime * result + ((mailingPolicy == null) ? 0 : mailingPolicy.hashCode());
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
		Project other = (Project)obj;
		if (collector == null)
		{
			if (other.collector != null)
				return false;
		}
		else if (!collector.equals(other.collector))
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
		if (peer == null)
		{
			if (other.peer != null)
				return false;
		}
		else if (!peer.equals(other.peer))
			return false;
		return true;
	}
	public List<Node> appInstances()
	{
		List<Node> $ = new ArrayList<Node>();
		for (Peer p : peer)
		{
			$.addAll(p.node);
		}
		return $;
	}
	
	
	
}
