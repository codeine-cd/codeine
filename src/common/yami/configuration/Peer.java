package yami.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Peer
{
	@XmlElement(name = "node")
	public List<Node> nodes = new ArrayList<Node>();
	@XmlAttribute
	public String name;
	public List<String> mailingList = new ArrayList<String>();
	
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
		return "Peer: name=" + name + ": " + nodes;
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
	
	public List<String> mailingList()
	{
		return mailingList;
	}
}
