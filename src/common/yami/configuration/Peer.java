package yami.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;

import yami.model.Constants;

public class Peer
{
	public List<Node> node = new ArrayList<Node>();
	public String dns_name;
	@XmlAttribute
	public String name;
	public List<String> mailingList = new ArrayList<String>();
	
	public Peer()
	{
		
	}
	
	public String getPeerLink()
	{
		GlobalConfiguration gc = ConfigurationManager.getInstance().getCurrentGlobalConfiguration();
		return "http://" + dnsName() + ":" + gc.getClientPort();
	}
	
	public String dnsName()
	{
	    return null == dns_name ? name : dns_name;
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

	@Override
	public String toString() {
	    return "Peer [node=" + node + ", dns_name=" + dns_name + ", name="
		    + name + ", mailingList=" + mailingList + "]";
	}
	
	

}
