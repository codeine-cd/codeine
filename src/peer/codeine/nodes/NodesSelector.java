package codeine.nodes;

import java.util.List;
import java.util.Map;
import java.util.Set;

import codeine.api.NodeInfo;
import codeine.executer.PeriodicExecuter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class NodesSelector {

	private Map<String, PeriodicExecuter> runningNodes;
	private List<NodeInfo> newNodes;

	public NodesSelector(Map<String, PeriodicExecuter> runningNodes, List<NodeInfo> newNodes) {
		this.runningNodes = runningNodes;
		this.newNodes = newNodes;
	}

	public SelectedNodes selectStartStop() {
		Map<String, PeriodicExecuter> existingProjectExecutors = Maps.newHashMap();
		List<NodeInfo> nodesToStart = Lists.newArrayList();
		Map<String, PeriodicExecuter> nodesToStop = Maps.newHashMap();
		
		Set<String> newAndCuerrentNodes = getNewAndCuerrentNodes();
		for (String nodeName : newAndCuerrentNodes) {
			if (shouldContinueRun(nodeName)) {
				if (!runningNodes.containsKey(nodeName)) {
					nodesToStart.add(getNode(nodeName));
				}
				else {
					existingProjectExecutors.put(nodeName, runningNodes.get(nodeName));
				}
				
			}
			else { //should not run
				nodesToStop.put(nodeName, runningNodes.get(nodeName));
			}
		}
		
		return new SelectedNodes(nodesToStop, nodesToStart, existingProjectExecutors);
	}

	private boolean shouldContinueRun(String nodeName) {
		for (NodeInfo n : newNodes) {
			if (n.name().equals(nodeName)) {
				return true;
			}
		}
		return false;
	}

	private NodeInfo getNode(String nodeName) {
		for (NodeInfo n : newNodes) {
			if (n.name().equals(nodeName)) {
				return n;
			}
		}
		throw new RuntimeException("error getting node name " + nodeName);
	}

	private Set<String> getNewAndCuerrentNodes() {
		Set<String> $ = Sets.newHashSet(runningNodes.keySet());
		for (NodeInfo n : newNodes) {
			$.add(n.name());
		}
		return $;
	}

}
