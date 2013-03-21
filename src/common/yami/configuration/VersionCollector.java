package yami.configuration;

public class VersionCollector extends HttpCollector
{
	
	public VersionCollector()
	{
		super();
		this.name = "version";
		this.includedNodes.add("all");
	}
	
	@Override
	public boolean hasStatus()
	{
		return false;
	}
	
}
