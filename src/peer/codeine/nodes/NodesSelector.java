package codeine.nodes;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import codeine.api.NodeInfo;
import codeine.executer.PeriodicExecuter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class NodesSelector {

	private static final Logger log = Logger.getLogger(NodesSelector.class);
	private Map<String, PeriodicExecuter> runningNodes;
	private List<NodeInfo> newNodes;

	public NodesSelector(Map<String, PeriodicExecuter> runningNodes, List<NodeInfo> newNodes) {
		this.runningNodes = runningNodes;
		this.newNodes = newNodes;
	}

	public SelectedNodes selectStartStop() {
		log.info("runningNodes " + runningNodes);
		Map<String, PeriodicExecuter> existingProjectExecutors = Maps.newHashMap();
		List<NodeInfo> nodesToStart = Lists.newArrayList();
		Map<String, PeriodicExecuter> nodesToStop = Maps.newHashMap();
		
		Set<String> newAndCuerrentNodes = getNewAndCuerrentNodes();
		log.info("newAndCuerrentNodes " + newAndCuerrentNodes);
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
		SelectedNodes $ = new SelectedNodes(nodesToStop, nodesToStart, existingProjectExecutors);
		log.info("returning " + $);
		return $;
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

	@Override
	public String toString() {
		return "NodesSelector [runningNodes=" + runningNodes + ", newNodes=" + newNodes + "]";
	}

	
}
