package yami.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class HttpCollector
{
	public String name;
	@XmlElement(name = "includedNode")
	public List<String> includedNodes = new ArrayList<String>();
	@XmlElement(name = "excludedNode")
	public List<String> excludedNodes = new ArrayList<String>();
	public List<String> dependsOn = new ArrayList<String>();
	@XmlElement(name = "rule")
	public List<CollectorRule> rules = new ArrayList<CollectorRule>();
	
	public HttpCollector()
	{
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dependsOn == null) ? 0 : dependsOn.hashCode());
		result = prime * result + ((excludedNodes == null) ? 0 : excludedNodes.hashCode());
		result = prime * result + ((includedNodes == null) ? 0 : includedNodes.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((rules == null) ? 0 : rules.hashCode());
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
		if (excludedNodes == null)
		{
			if (other.excludedNodes != null)
				return false;
		}
		else if (!excludedNodes.equals(other.excludedNodes))
			return false;
		if (includedNodes == null)
		{
			if (other.includedNodes != null)
				return false;
		}
		else if (!includedNodes.equals(other.includedNodes))
			return false;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		if (rules == null)
		{
			if (other.rules != null)
				return false;
		}
		else if (!rules.equals(other.rules))
			return false;
		return true;
	}
	
	public List<HttpCollector> dependsOn()
	{
		final List<HttpCollector> l = new ArrayList<HttpCollector>();
		Project p = ConfigurationManager.getInstance().getConfiguredProject();
		for (HttpCollector c : p.collectors)
		{
			for (String name : dependsOn)
			{
				if (name.equals(c.name))
				{
					l.add(c);
				}
			}
		}
		return l;
	}
	
	@Override
	public String toString()
	{
		return "HttpCollector [name=" + name + "]";
	}
}
