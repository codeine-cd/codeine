package yami.configuration;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author oshai currently names has to be unique
 */
public class Node
{
	public String nick;
	@XmlAttribute
	public String name;
	@XmlTransient
	public Peer peer;
	
	public Node(String name, String nick, Peer peer)
	{
		this.name = name;
		this.nick = nick;
		this.peer = peer;
	}
	
	public Node(String nick)
	{
		this.nick = nick;
		this.name = nick;
	}
	
	public Node()
	{
		
	}
	
	public void afterUnmarshal(Unmarshaller u, Object parent)
	{
		peer = (Peer)parent;
	}
	
	public String nick()
	{
		if (null == nick || "".equals(nick))
		{
			return name;
		}
		return nick;
	}
	
	@Override
	public String toString()
	{
		return "Node [nick=" + nick + ", name=" + name + "]";
	}
	
	public String getLogLink()
	{
		return "http://" + peer.dnsName() + ":" + ConfigurationManager.getInstance().getCurrentGlobalConfiguration().getClientPort();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((nick == null) ? 0 : nick.hashCode());
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
		Node other = (Node)obj;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		if (nick == null)
		{
			if (other.nick != null)
				return false;
		}
		else if (!nick.equals(other.nick))
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
