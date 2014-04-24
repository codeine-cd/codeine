package codeine.db;

import java.util.Map;

import codeine.jsons.peer_status.PeerStatusJsonV2;

public interface IStatusDatabaseConnector {

	void putReplaceStatus(PeerStatusJsonV2 p);
	public void updatePeersStatus(final long timeToRemove, final long timeToDisc);
	Map<String, PeerStatusJsonV2> getPeersStatus();
	String server();
}
