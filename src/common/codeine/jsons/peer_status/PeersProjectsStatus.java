package codeine.jsons.peer_status;

import java.util.Map;

import codeine.executer.Task;

public interface PeersProjectsStatus extends Task {
	
	public Map<String, PeerStatusJsonV2> peer_to_projects();

}
