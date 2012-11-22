package yami.configuration;

import java.util.ArrayList;
import java.util.List;

public class HttpCollector
{
	
	public String name;
	public List<String> includedNode = new ArrayList<String>();
	public List<String> excludedNode = new ArrayList<String>();
	
	public HttpCollector()
	{
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((excludedNode == null) ? 0 : excludedNode.hashCode());
		result = prime * result + ((includedNode == null) ? 0 : includedNode.hashCode());
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
		HttpCollector other = (HttpCollector)obj;
		if (excludedNode == null)
		{
			if (other.excludedNode != null)
				return false;
		}
		else if (!excludedNode.equals(other.excludedNode))
			return false;
		if (includedNode == null)
		{
			if (other.includedNode != null)
				return false;
		}
		else if (!includedNode.equals(other.includedNode))
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
	
}
