package codeine.nodes;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import codeine.api.NodeInfo;
import codeine.executer.PeriodicExecuter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class NodesSelectorTest {

	Map<NodeInfo, PeriodicExecuter> runningNodes = Maps.newHashMap();
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
		NodeInfo node1 = new NodeInfo("1");
		newNodes.add(node1);
		runningNodes.put(node1, mock(PeriodicExecuter.class));
		SelectedNodes result = create().selectStartStop();
		assertEquals(Lists.newArrayList(), result.nodesToStart());
		assertEquals(runningNodes, result.existingProjectExecutors());
	}
	@Test
	public void testNewNodeAlreadyExistsWithDifferentAlias() {
		NodeInfo newNode = new NodeInfo("1", "?");
		newNodes.add(newNode);
		runningNodes.put(new NodeInfo("1", "!"), mock(PeriodicExecuter.class));
		SelectedNodes result = create().selectStartStop();
		assertEquals(Lists.newArrayList(newNode), result.nodesToStart());
		assertEquals(Maps.newHashMap(), result.existingProjectExecutors());
		assertEquals(runningNodes, result.nodesToStop());
	}
	@Test
	public void testNewNodeAlreadyExistsWithDifferentTags() {
		NodeInfo newNode = new NodeInfo("1");
		newNode.tags(Lists.newArrayList("a"));
		newNodes.add(newNode);
		runningNodes.put(new NodeInfo("1"), mock(PeriodicExecuter.class));
		SelectedNodes result = create().selectStartStop();
		assertEquals(Lists.newArrayList(newNode), result.nodesToStart());
		assertEquals(Maps.newHashMap(), result.existingProjectExecutors());
		assertEquals(runningNodes, result.nodesToStop());
	}
	@Test
	public void testQA() {
		NodeInfo nodeToStart = new NodeInfo("1");
		newNodes.add(nodeToStart);
		NodeInfo node2 = new NodeInfo("2");
		newNodes.add(node2);
		PeriodicExecuter mock2 = mock(PeriodicExecuter.class);
		runningNodes.put(node2, mock2);
		PeriodicExecuter mock3 = mock(PeriodicExecuter.class);
		runningNodes.put(node("3"), mock3);
		HashMap<Object, Object> nodeSToStop = Maps.newHashMap();
		nodeSToStop.put(node("3"), mock3);
		HashMap<Object, Object> nodesToKeep = Maps.newHashMap();
		nodesToKeep.put(node2, mock2);
		SelectedNodes result = create().selectStartStop();
		assertEquals(Lists.newArrayList(nodeToStart), result.nodesToStart());
		assertEquals(nodeSToStop, result.nodesToStop());
		assertEquals(nodesToKeep, result.existingProjectExecutors());
	}
	@Test
	public void testRemoveNode() {
		runningNodes.put(node("1"), mock(PeriodicExecuter.class));
		SelectedNodes result = create().selectStartStop();
		assertEquals(runningNodes, result.nodesToStop());
		assertEquals(Maps.newHashMap(), result.existingProjectExecutors());
	}
	private NodeInfo node(String name) {
		return new NodeInfo(name);
	}

}
