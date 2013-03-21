package yami.configuration;

public class KeepaliveCollector extends HttpCollector
{
	
	public KeepaliveCollector()
	{
		super();
		this.name = "keepalive";
		this.includedNodes.add("all");
	}
	
	@Override
	public boolean hasStatus()
	{
		return false;
	}
	
}
