package codeine.command_peer;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import codeine.api.NodeGetter;
import codeine.api.NodeWithMonitorsInfo;
import codeine.api.NodeWithPeerInfo;
import codeine.api.ScehudleCommandExecutionInfo;
import codeine.configuration.Links;
import codeine.jsons.project.ProjectJson;
import codeine.permissions.IUserWithPermissions;
import codeine.utils.StringUtils;
import codeine.utils.ThreadUtils;

import com.google.common.collect.Lists;

public class ProgressiveExecutionStrategy extends CommandExecutionStrategy {

	private static final Logger log = Logger.getLogger(ProgressiveExecutionStrategy.class);
	private NodeGetter nodesGetter;
	private Object cancelObject = new Object();

	public ProgressiveExecutionStrategy(AllNodesCommandExecuter allNodesCommandExecuter,ScehudleCommandExecutionInfo commandData, Links links, NodeGetter nodesGetter, ProjectJson project, IUserWithPermissions userObject) {
		super(commandData, allNodesCommandExecuter, links, project, userObject);
		this.nodesGetter = nodesGetter;
	}


	@Override
	public void execute() {
		List<NodeWithPeerInfo> leftNodes = Lists.newArrayList(commandData().nodes());
		long endTime = System.currentTimeMillis() + durationInMillis();
		ProgressiveRateClaculator calc;
		switch (commandData().command_info().ratio()){
		case Linear: {
			calc = new LinearProgressiveRateClaculator();
			break;
		}
		case Exponential: {
			calc = new ExponentialProgressiveRateClaculator(leftNodes.size(), durationInMillis());
			break;
		}
		default: throw new RuntimeException("no ratio implementation: " + commandData().command_info().ratio());
		}
		List<NodeWithPeerInfo> completedNodes = Lists.newArrayList();
		while (leftNodes.size() > 0 && !isCancel()) {
			if (commandData().command_info().stop_on_error() && completedNodes.size() > 0) {
				List<NodeWithMonitorsInfo> nodes = nodesGetter.getNodes(commandData().command_info().project_name(), completedNodes);
				List<String> failedNodes = Lists.newArrayList();
				for (NodeWithMonitorsInfo nodeInfo : nodes) {
					if (!nodeInfo.status()){
						failedNodes.add(nodeInfo.alias());
					}
				}
				int errorPercent = failedNodes.size() * 100 / completedNodes.size();
				if (errorPercent > commandData().command_info().error_percent_val()) {
					writeLine("nodes with error: " + failedNodes);
					writeLine("Execution stopped because of errors. error percent is " + errorPercent + "% number of nodes with error: " + failedNodes.size());
					return;
				}
			}
			long startLoop = System.currentTimeMillis();
			double minutesLeft = (double) TimeUnit.MILLISECONDS.toMinutes(endTime - System.currentTimeMillis());
			calc.iterationStart(minutesLeft, leftNodes.size());
			int numOfNodesToExecute = calc.numOfNodesToExecute();
			writeLine("Will execute on " + numOfNodesToExecute + " nodes");
			if (numOfNodesToExecute < CommandExecutionStrategy.MAX_NODES_TO_EXECUTE) {
				log.info("numOfNodesToExecute is above limit " + numOfNodesToExecute);
				writeLine("execution concurrency is above limit, will set it to " + CommandExecutionStrategy.MAX_NODES_TO_EXECUTE);
				numOfNodesToExecute = CommandExecutionStrategy.MAX_NODES_TO_EXECUTE;
			}
			List<NodeWithPeerInfo> subList = leftNodes.subList(0, numOfNodesToExecute);
			executeConcurrent(subList, numOfNodesToExecute);
			completedNodes.addAll(subList);
			subList.clear();
			long loopTime = System.currentTimeMillis() - startLoop;
			long sleepTime = calc.getTimeToSleep(loopTime);
			if ((sleepTime > 0) && (leftNodes.size() > 0)) {
				writeLine("Execution of " + numOfNodesToExecute + " nodes took " + StringUtils.formatTimePeriod(loopTime) + ", going to sleep for " + StringUtils.formatTimePeriod(sleepTime));
				ThreadUtils.wait(cancelObject, sleepTime);
			} else {
				writeLine("Execution of " + numOfNodesToExecute + " nodes took " + StringUtils.formatTimePeriod(loopTime) + ", will not go to sleep");
			}
		}
		log.info("done executing");
	}
	
	private long durationInMillis() {
		switch (commandData().command_info().duration_units()) {
			case Days:
				return  TimeUnit.DAYS.toMillis(commandData().command_info().duration());
			case Hours:
				return  TimeUnit.HOURS.toMillis(commandData().command_info().duration());
			case Minutes:
				return  TimeUnit.MINUTES.toMillis(commandData().command_info().duration());
		default:
			throw new UnsupportedOperationException("unknown duration units " + commandData().command_info().duration_units());
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
