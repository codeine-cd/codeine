package yami.configuration;

public class InternalNode extends Node
{

	public InternalNode(Peer peer)
	{
		super("yami_internal_node", "yami_internal_node", peer); 
	}
	
	@Override
	public String nick()
	{
		return peer.dnsName();
	}
}
