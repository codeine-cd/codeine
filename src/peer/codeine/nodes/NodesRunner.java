package codeine.nodes;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.PeerStatusChangedUpdater;
import codeine.RunMonitors;
import codeine.SnoozeKeeper;
import codeine.api.NodeInfo;
import codeine.configuration.IConfigurationManager;
import codeine.configuration.PathHelper;
import codeine.executer.PeriodicExecuter;
import codeine.executer.Task;
import codeine.jsons.nodes.NodesManager;
import codeine.jsons.peer_status.PeerStatus;
import codeine.jsons.project.ProjectJson;
import codeine.mail.MailSender;
import codeine.mail.NotificationDeliverToMongo;
import codeine.utils.network.InetUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class NodesRunner implements Task{

	private static final Logger log = Logger.getLogger(NodesRunner.class);
	
	private static final long NODE_MONITOR_INTERVAL = TimeUnit.SECONDS.toMillis(29);
	public static final long NODE_RUNNER_INTERVAL = TimeUnit.HOURS.toMillis(1);
	
	private String hostname = InetUtils.getLocalHost().getHostName();
	@Inject	private IConfigurationManager configurationManager;
	@Inject	private PathHelper pathHelper;
	@Inject	private PeerStatus projectStatusUpdater;
	@Inject	private MailSender mailSender;
	@Inject	private NotificationDeliverToMongo notificationDeliverToMongo;
	@Inject	private NodesManager nodesManager;
	@Inject	private SnoozeKeeper snoozeKeeper;
	private Map<String, Map<String, PeriodicExecuter>> executers = Maps.newHashMap();
	@Inject	private PeerStatusChangedUpdater mongoPeerStatusUpdater;
	
	@Override
	public synchronized void run() {
		List<String> removedProjects = Lists.newArrayList(executers.keySet());
		for (ProjectJson project : getProjects()) {
			boolean hasExecuters = startExecutorsForProject(project);
			if (hasExecuters) {
				removedProjects.remove(project.name());
			}
		}
		for (String project : removedProjects) {
			Map<String, PeriodicExecuter> map = executers.get(project);
			for (PeriodicExecuter e : map.values()) {
				stop(e);
			}
		}
	}

	private void stop(PeriodicExecuter e) {
		log.info("stopping 1executor " + e.name());
		e.stopWhenPossible();
	}

	private boolean startExecutorsForProject(ProjectJson project) {
		Map<String, PeriodicExecuter> newProjectExecutors = Maps.newHashMap();
		Map<String, PeriodicExecuter> oldProjectExecutors = executers.put(project.name(), newProjectExecutors);
		if (null == oldProjectExecutors){
			oldProjectExecutors = Maps.newHashMap();
		}
		try {
			List<NodeInfo> nodes = getNodes(project);
			if (nodes.isEmpty()) {
				log.info("ignoring project " + project.name() + ". not configured to run on host " + hostname);
				return false;
			}
			for (NodeInfo nodeJson : nodes) {
				// if (node.disabled())
				// {
				// log.info("node is disabled " + node.name);
				// continue;
				// }
				PeriodicExecuter e = oldProjectExecutors.remove(nodeJson.name());
				if (e == null){
					e = startExecuter(project, nodeJson);
				}
				newProjectExecutors.put(nodeJson.name(), e);
			}
		} catch (Exception e) {
			log.error("failed to configure project " + project.name() + " ,will ignore project", e);
		}
		for (Entry<String, PeriodicExecuter> e : oldProjectExecutors.entrySet()) {
			log.info("stop monitoring node " + e.getKey() + " in project " + project.name());
			stop(e.getValue());
		}
		return !newProjectExecutors.isEmpty();
	}

	private PeriodicExecuter startExecuter(ProjectJson project, NodeInfo nodeJson) {
		log.info("Starting monitor thread for project " + project.name() + " node " + nodeJson);
		PeriodicExecuter periodicExecuter = new PeriodicExecuter(NODE_MONITOR_INTERVAL, 
				new RunMonitors(configurationManager, project.name(), projectStatusUpdater, mailSender, pathHelper,
				nodeJson, notificationDeliverToMongo, mongoPeerStatusUpdater, snoozeKeeper), "RunMonitors_" + project.name() + "_" + nodeJson.name());
		log.info("starting 1executor " + periodicExecuter.name());
		periodicExecuter.runInThread();
		return periodicExecuter;
	}

	private List<ProjectJson> getProjects() {
		return configurationManager.getConfiguredProjects();
	}

	private List<NodeInfo> getNodes(ProjectJson project) {
		try {
			return nodesManager.nodesOf(project).nodes();
		} catch (Exception e) {
			log.warn("failed to get nodes for project " + project.name(), e);
		}
		return Lists.newArrayList();
	}

}
