package yami.configuration;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlTransient;

import yami.model.Constants;

/**
 * @author oshai
 * currently names has to be unique
 */
public class Node
{
	public String nick;
	public String name;
	@XmlTransient
	public Peer peer;
	
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
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
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
		Node other = (Node)obj;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "Node [nick=" + nick + ", name=" + name + "]";
	}

	public String getLogLink()
	{
		return "http://" + peer.name + ":"+ Constants.getClientPort();
	}
}
