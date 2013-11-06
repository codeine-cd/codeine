package codeine.configuration;

public class InternalNode extends Node
{

	public InternalNode(Peer peer)
	{
		super("codeine_internal", "codeine_internal", peer); 
	}
	
	@Override
	public String nick()
	{
		return peer.dnsName();
	}
}
