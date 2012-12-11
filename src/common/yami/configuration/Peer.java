package yami.configuration;

import java.util.*;

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
		return "Peer: name=" + name + ": " + node;
	}
	
	public String getPeerLink()
	{
		GlobalConfiguration gc = ConfigurationManager.getInstance().getCurrentGlobalConfiguration();
		return "http://" + name + ":" + gc.getClientPort(); 
	}
	
	public String getPeerRestartLink()
	{
		return getPeerLink() + "/restart"; 
	}
}
