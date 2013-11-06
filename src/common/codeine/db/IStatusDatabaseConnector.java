package codeine.db;

import java.util.Map;

import codeine.jsons.peer_status.PeerStatusJsonV2;

public interface IStatusDatabaseConnector {

	void putReplaceStatus(PeerStatusJsonV2 p);
	int removeExpiredPeers(int timeToLive);
	int updatePeerStatusToDisconnected(int timeToLive); 
	Map<String, PeerStatusJsonV2> getPeersStatus();
}
