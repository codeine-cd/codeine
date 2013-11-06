package codeine.api;

public class NodeWithPeerInfo extends NodeInfo{

	private String peerName;

	public NodeWithPeerInfo(String name, String alias, String peerName) {
		super(name, alias);
		this.peerName = peerName;
	}

	public String peerName() {
		return peerName;
	}
}
