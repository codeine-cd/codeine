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
	private Map<NodeInfo, PeriodicExecuter> runningNodes;
	private List<NodeInfo> newNodes;

	public NodesSelector(Map<NodeInfo, PeriodicExecuter> runningNodes, List<NodeInfo> newNodes) {
		this.runningNodes = runningNodes;
		this.newNodes = newNodes;
	}

	public SelectedNodes selectStartStop() {
		log.debug("runningNodes " + runningNodes);
		Map<NodeInfo, PeriodicExecuter> existingProjectExecutors = Maps.newHashMap();
		List<NodeInfo> nodesToStart = Lists.newArrayList();
		Map<NodeInfo, PeriodicExecuter> nodesToStop = Maps.newHashMap();
		
		Set<NodeInfo> newAndCuerrentNodes = getNewAndCuerrentNodes();
		log.debug("newAndCuerrentNodes " + newAndCuerrentNodes);
		for (NodeInfo node : newAndCuerrentNodes) {
			if (shouldContinueRun(node)) {
				if (!runningNodes.containsKey(node)) {
					nodesToStart.add(getNode(node));
				}
				else {
					existingProjectExecutors.put(node, runningNodes.get(node));
				}
				
			}
			else { //should not run
				nodesToStop.put(node, runningNodes.get(node));
			}
		}
		SelectedNodes $ = new SelectedNodes(nodesToStop, nodesToStart, existingProjectExecutors);
		log.info("returning " + $);
		return $;
	}

	private boolean shouldContinueRun(NodeInfo node) {
		for (NodeInfo n : newNodes) {
			if (n.equals(node)) {
				return true;
			}
		}
		return false;
	}

	private NodeInfo getNode(NodeInfo node) {
		for (NodeInfo n : newNodes) {
			if (n.equals(node)) {
				return n;
			}
		}
		throw new RuntimeException("error getting node name " + node);
	}

	private Set<NodeInfo> getNewAndCuerrentNodes() {
		Set<NodeInfo> $ = Sets.newHashSet(runningNodes.keySet());
		for (NodeInfo n : newNodes) {
			$.add(n);
		}
		return $;
	}

	@Override
	public String toString() {
		return "NodesSelector [runningNodes=" + runningNodes + ", newNodes=" + newNodes + "]";
	}

	
}
