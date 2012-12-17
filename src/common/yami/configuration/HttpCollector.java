package yami.configuration;

import java.util.*;

public class HttpCollector
{
	
	public String name;
	public List<String> includedNode = new ArrayList<String>();
	public List<String> excludedNode = new ArrayList<String>();
	public List<String> dependsOn = new ArrayList<String>();
	public List<CollectorRule> rule = new ArrayList<CollectorRule>();
	
	public HttpCollector()
	{
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dependsOn == null) ? 0 : dependsOn.hashCode());
		result = prime * result + ((excludedNode == null) ? 0 : excludedNode.hashCode());
		result = prime * result + ((includedNode == null) ? 0 : includedNode.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((rule == null) ? 0 : rule.hashCode());
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
		if (dependsOn == null)
		{
			if (other.dependsOn != null)
				return false;
		}
		else if (!dependsOn.equals(other.dependsOn))
			return false;
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
		if (rule == null)
		{
			if (other.rule != null)
				return false;
		}
		else if (!rule.equals(other.rule))
			return false;
		return true;
	}


	public List<HttpCollector> dependsOn()
	{
		final List<HttpCollector> l = new ArrayList<HttpCollector>();
		Project p = ConfigurationManager.getInstance().getConfiguredProject();
		for (HttpCollector c : p.collector)
		{
			for (String name : dependsOn)
			{
				if (name.equals(c.name)) {
					l.add(c);
				}
			}
		}
		return l;
	}
	
}
