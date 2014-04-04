package codeine.nodes;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import codeine.api.NodeInfo;
import codeine.executer.PeriodicExecuter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class NodesSelectorTest {

	Map<String, PeriodicExecuter> runningNodes = Maps.newHashMap();
	List<NodeInfo> newNodes = Lists.newArrayList();

	@Test
	public void testEmpty() {
		SelectedNodes result = create().selectStartStop();
		assertEquals(newNodes, result.nodesToStart());
		assertEquals(runningNodes, result.nodesToStop());
		assertEquals(runningNodes, result.existingProjectExecutors());
	}
	private NodesSelector create() {
		return new NodesSelector(runningNodes, newNodes);
	}
	@Test
	public void testNewNode() {
		newNodes.add(new NodeInfo("1"));
		SelectedNodes result = create().selectStartStop();
		assertEquals(newNodes, result.nodesToStart());
		assertEquals(Maps.newHashMap(), result.existingProjectExecutors());
	}
	@Test
	public void testNewNodeAlreadyExists() {
		newNodes.add(new NodeInfo("1"));
		runningNodes.put("1", mock(PeriodicExecuter.class));
		SelectedNodes result = create().selectStartStop();
		assertEquals(Lists.newArrayList(), result.nodesToStart());
		assertEquals(runningNodes, result.existingProjectExecutors());
	}
	@Test
	public void testQA() {
		NodeInfo nodeToStart = new NodeInfo("1");
		newNodes.add(nodeToStart);
		newNodes.add(new NodeInfo("2"));
		PeriodicExecuter mock2 = mock(PeriodicExecuter.class);
		runningNodes.put("2", mock2);
		PeriodicExecuter mock3 = mock(PeriodicExecuter.class);
		runningNodes.put("3", mock3);
		HashMap<Object, Object> nodeSToStop = Maps.newHashMap();
		nodeSToStop.put("3", mock3);
		HashMap<Object, Object> nodesToKeep = Maps.newHashMap();
		nodesToKeep.put("2", mock2);
		SelectedNodes result = create().selectStartStop();
		assertEquals(Lists.newArrayList(nodeToStart), result.nodesToStart());
		assertEquals(nodeSToStop, result.nodesToStop());
		assertEquals(nodesToKeep, result.existingProjectExecutors());
	}
	@Test
	public void testRemoveNode() {
		runningNodes.put("1", mock(PeriodicExecuter.class));
		SelectedNodes result = create().selectStartStop();
		assertEquals(runningNodes, result.nodesToStop());
		assertEquals(Maps.newHashMap(), result.existingProjectExecutors());
	}

}
