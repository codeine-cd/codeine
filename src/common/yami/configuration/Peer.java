package yami.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import yami.model.Constants;

public class Peer
{
	@XmlElement(name = "node")
	public List<Node> nodes = new ArrayList<Node>();
	public String dnsName;
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
		return "http://" + dnsName() + ":" + gc.getClientPort();
	}
	
	public String dnsName()
	{
	    return null == dnsName ? name : dnsName;
	}
	
	public String getPeerRestartLink()
	{
		return getPeerLink() + Constants.RESTART_CONTEXT;
	}
	public String getPeerSwitchVersionLink(String node, String version)
	{
	    return getPeerCommandLink(node, "switch-version") + "&version=" + version;
	}
	public String getPeerCommandLink(String node, String command) 
	{
	    return getPeerLink() + Constants.COMMAND_NODE_CONTEXT + "?node=" + node + "&command=" + command;
	}
	
	public List<String> mailingList()
	{
		return mailingList;
	}

}
