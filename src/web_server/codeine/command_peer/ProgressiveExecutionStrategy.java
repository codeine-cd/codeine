package codeine.command_peer;

import java.util.List;
import java.util.concurrent.TimeUnit;

import codeine.api.NodeDataJson;
import codeine.api.NodeGetter;
import codeine.api.NodeWithMonitorsInfo;
import codeine.configuration.Links;
import codeine.utils.StringUtils;
import codeine.utils.ThreadUtils;

import com.google.common.collect.Lists;

public class ProgressiveExecutionStrategy extends CommandExecutionStrategy {

	private NodeGetter nodesGetter;
	private Object cancelObject = new Object();

	public ProgressiveExecutionStrategy(AllNodesCommandExecuter allNodesCommandExecuter,ScehudleCommandPostData commandData, Links links, NodeGetter nodesGetter) {
		super(allNodesCommandExecuter, commandData, links);
		this.nodesGetter = nodesGetter;
	}

	@Override
	public void execute() {
		List<NodeDataJson> leftNodes = Lists.newArrayList(commandData().nodes());
		long endTime = System.currentTimeMillis() + durationInMillis();
		TimeToSleepCalculator timeCalc = new TimeToSleepCalculator();
		List<NodeDataJson> completedNodes = Lists.newArrayList();
		while (leftNodes.size() > 0 && !isCancel()) {
			if (commandData().stopOnError() && completedNodes.size() > 0) {
				List<NodeWithMonitorsInfo> nodes = nodesGetter.getNodes(commandData().project_name(), completedNodes);
				List<String> failedNodes = Lists.newArrayList();
				for (NodeWithMonitorsInfo nodeInfo : nodes) {
					if (!nodeInfo.status()){
						failedNodes.add(nodeInfo.alias());
					}
				}
				int errorPercent = failedNodes.size() / (completedNodes.size()) * 100;
				if (errorPercent > commandData().errorPercent()) {
					writeLine("nodes with error: " + failedNodes);
					writeLine("Execution stopped because of errors. error percent is " + errorPercent + "% number of nodes with error: " + failedNodes.size());
					return;
				}
			}
			long startLoop = System.currentTimeMillis();
			double minutesLeft = (double) TimeUnit.MILLISECONDS.toMinutes(endTime - System.currentTimeMillis());
			RatioCalculator ratioCalc = new RatioCalculator(minutesLeft, leftNodes.size());
			writeLine("Will execute on " + ratioCalc.concerency() + " nodes");
			List<NodeDataJson> subList = leftNodes.subList(0, ratioCalc.concerency());
			executeConcurrent(subList, ratioCalc.concerency());
			completedNodes.addAll(subList);
			subList.clear();
			long loopTime = System.currentTimeMillis() - startLoop;
			long sleepTime = timeCalc.getTimeToSleep(ratioCalc.ratio(), loopTime);
			if ((sleepTime > 0) && (leftNodes.size() > 0)) {
				writeLine("Execution of " + ratioCalc.concerency() + " nodes took " + StringUtils.formatTimePeriod(loopTime) + ", going to sleep for " + StringUtils.formatTimePeriod(sleepTime));
				ThreadUtils.wait(cancelObject, sleepTime);
			} else {
				writeLine("Execution of " + ratioCalc.concerency() + " nodes took " + StringUtils.formatTimePeriod(loopTime) + ", will not go to sleep");
			}
		}
	}
	
	private long durationInMillis() {
		switch (commandData().durationUnits()) {
			case Days:
				return  TimeUnit.DAYS.toMillis(commandData().duration());
			case Hours:
				return  TimeUnit.HOURS.toMillis(commandData().duration());
		default:
			throw new UnsupportedOperationException("unknown duration units " + commandData().durationUnits());
		}
	}

	
	@Override
	public void setCancel() {
		super.setCancel();
		synchronized (cancelObject) {
			cancelObject.notifyAll();
		}
	}
}
