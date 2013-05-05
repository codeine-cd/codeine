package yami.servlets;

import static com.google.common.collect.Lists.*;

import java.util.List;

import yami.configuration.Node;

public class VersionItem
{

	private final String m_version;
	private List<Node> nodes = newArrayList();

	public VersionItem(String version)
	{
		m_version = version;
	}

	public void add(Node node)
	{
		nodes.add(node);	
	}

	public String version()
	{
		return m_version;
	}

	public int count()
	{
		return nodes.size();
	}
	
}
