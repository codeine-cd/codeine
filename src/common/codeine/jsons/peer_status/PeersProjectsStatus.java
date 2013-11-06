package codeine.jsons.peer_status;

import java.util.Map;

import com.google.common.collect.Maps;

public class PeersProjectsStatus {

	public Map<String, PeerStatusJsonV2> peer_to_projects = Maps.newHashMap();//Lists.newArrayList();

	public void replaceMap(Map<String, PeerStatusJsonV2> newMap) {
		peer_to_projects = newMap;
	}

	public Map<String, PeerStatusJsonV2> peer_to_projects() {
		return peer_to_projects;
	}
}
