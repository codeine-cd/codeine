package codeine.nodes;

import java.util.List;
import java.util.Map;

import codeine.api.NodeInfo;
import codeine.executer.PeriodicExecuter;

public class SelectedNodes {

	private Map<String, PeriodicExecuter> nodesToStop;
	private List<NodeInfo> nodesToStart;
	private Map<String, PeriodicExecuter> existingProjectExecutors;
	
	public SelectedNodes(Map<String, PeriodicExecuter> nodesToStop, List<NodeInfo> nodesToStart,
			Map<String, PeriodicExecuter> existingProjectExecutors) {
		super();
		this.nodesToStop = nodesToStop;
		this.nodesToStart = nodesToStart;
		this.existingProjectExecutors = existingProjectExecutors;
	}

	public Map<String, PeriodicExecuter> existingProjectExecutors() {
		return existingProjectExecutors;
	}

	public List<NodeInfo> nodesToStart() {
		return nodesToStart;
	}
	public Map<String, PeriodicExecuter> nodesToStop() {
		return nodesToStop;
	}

	@Override
	public String toString() {
		return "SelectedNodes [nodesToStop=" + nodesToStop + ", nodesToStart=" + nodesToStart
				+ ", existingProjectExecutors=" + existingProjectExecutors + "]";
	}

	
}
