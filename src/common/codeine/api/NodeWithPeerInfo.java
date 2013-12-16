package codeine.api;

import codeine.jsons.peer_status.PeerStatusJsonV2;

public class NodeWithPeerInfo extends NodeInfo{

	private transient PeerStatusJsonV2 peer;
	private String peer_address;

	public NodeWithPeerInfo(String name, String alias, PeerStatusJsonV2 peer) {
		super(name, alias);
		this.peer = peer;
		if (null != peer) {
			peer_address = peer.host_port();
		}
	}

	public PeerStatusJsonV2 peer() {
		return peer;
	}
	
	public void peer(PeerStatusJsonV2 peer) {
		this.peer = peer;
		peer_address = peer.host_port();
	}

	public String peer_address() {
		return peer_address;
	}
}
