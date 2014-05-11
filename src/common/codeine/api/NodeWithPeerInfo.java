package codeine.api;

import codeine.jsons.peer_status.PeerStatusJsonV2;
import codeine.jsons.peer_status.PeerStatusString;

public class NodeWithPeerInfo extends NodeInfo{

	private transient PeerStatusJsonV2 peer;
	private String peer_address;
	private String peer_host_port;
	private String peer_key;
	private PeerStatusString peer_status;

	public NodeWithPeerInfo(String name, String alias, PeerStatusJsonV2 peer) {
		super(name, alias);
		this.peer = peer;
		if (null != peer) {
			peer_host_port = peer.host_port();
			peer_address = peer.address_port();
			peer_key = peer.key();
			peer_status = peer.status();
		}
	}

	public PeerStatusJsonV2 peer() {
		return peer;
	}
	
	public void peer(PeerStatusJsonV2 peer) {
		this.peer = peer;
		peer_host_port = peer.host_port();
		peer_address = peer.address_port();
		peer_key = peer.key();
		peer_status = peer.status();
	}

	public String peer_address() {
		return peer_address;
	}
	
	public String peer_key() {
		return peer_key;
	}

	public PeerStatusString peer_status() {
		return peer_status;
	}
	
	public void peer_address(String peer_address) {
		this.peer_address = peer_address;
	}

	@Override
	public String toString() {
		return "NodeWithPeerInfo [peer_address=" + peer_address + ", peer_host_port=" + peer_host_port + ", peer_key="
				+ peer_key + ", peer_status=" + peer_status + "]";
	}
	
	
}
