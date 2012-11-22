package yami.configuration;

import java.util.ArrayList;
import java.util.List;

public class Peer
{
	public List<Node> node = new ArrayList<Node>();
	public String name;
	
	public Peer()
	{
	}
	
	public Peer(String name)
	{
		this.name = name;
	}
	
	@Override
	public String toString()
	{
		return "Peer: name="+name+": "+node;
	}
}
