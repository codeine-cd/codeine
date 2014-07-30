package codeine.nodes;

import java.util.List;
import java.util.Map;

import codeine.api.NodeInfo;
import codeine.executer.PeriodicExecuter;

public class SelectedNodes {

	private Map<NodeInfo, PeriodicExecuter> nodesToStop;
	private List<NodeInfo> nodesToStart;
	private Map<NodeInfo, PeriodicExecuter> existingProjectExecutors;
	
	public SelectedNodes(Map<NodeInfo, PeriodicExecuter> nodesToStop, List<NodeInfo> nodesToStart,
			Map<NodeInfo, PeriodicExecuter> existingProjectExecutors) {
		super();
		this.nodesToStop = nodesToStop;
		this.nodesToStart = nodesToStart;
		this.existingProjectExecutors = existingProjectExecutors;
	}

	public Map<NodeInfo, PeriodicExecuter> existingProjectExecutors() {
		return existingProjectExecutors;
	}

	public List<NodeInfo> nodesToStart() {
		return nodesToStart;
	}
	public Map<NodeInfo, PeriodicExecuter> nodesToStop() {
		return nodesToStop;
	}

	@Override
	public String toString() {
		return "SelectedNodes [nodesToStop=" + nodesToStop + ", nodesToStart=" + nodesToStart
				+ ", existingProjectExecutors=" + existingProjectExecutors + "]";
	}

	
}
