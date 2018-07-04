package codeine.nodes;

import codeine.jsons.global.GlobalConfigurationJsonStore;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.PeerStatusChangedUpdater;
import codeine.SnoozeKeeper;
import codeine.api.NodeInfo;
import codeine.collectors.CollectorsRunner;
import codeine.collectors.CollectorsRunnerFactory;
import codeine.configuration.IConfigurationManager;
import codeine.configuration.PathHelper;
import codeine.executer.PeriodicExecuter;
import codeine.executer.Task;
import codeine.jsons.nodes.NodesManager;
import codeine.jsons.peer_status.PeerStatus;
import codeine.jsons.project.ProjectJson;
import codeine.mail.MailSender;
import codeine.mail.NotificationDeliverToDatabase;
import codeine.model.Constants;
import codeine.utils.network.InetUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class NodesRunner implements Task {

    private static final Logger log = Logger.getLogger(NodesRunner.class);

    private static final long NODE_MONITOR_INTERVAL = TimeUnit.SECONDS.toMillis(29);
    public static final long NODE_RUNNER_INTERVAL = TimeUnit.HOURS.toMillis(1);

    @Inject
    private IConfigurationManager configurationManager;
    @Inject
    private PathHelper pathHelper;
    @Inject
    private PeerStatus peerStatus;
    @Inject
    private MailSender mailSender;
    @Inject
    private NotificationDeliverToDatabase notificationDeliverToMongo;
    @Inject
    private NodesManager nodesManager;
    @Inject
    private SnoozeKeeper snoozeKeeper;
    private Map<String, Map<NodeInfo, PeriodicExecuter>> executers = Maps.newHashMap();
    @Inject
    private PeerStatusChangedUpdater mongoPeerStatusUpdater;
    @Inject
    private CollectorsRunnerFactory collectorsRunnerFactory;
    @Inject
    private GlobalConfigurationJsonStore globalConfigurationJsonStore;

    @Override
    public synchronized void run() {
        InetAddress localHost = InetUtils.getLocalHost();
        log.info("NodeRunner is starting on host " + localHost.getHostName() + " " + localHost
            .getCanonicalHostName());
        log.info("NodeRunner is starting " + this + " with executers " + executers);
        Set<String> removedProjects = Sets.newHashSet(executers.keySet());
        for (ProjectJson project : getProjects()) {
            removedProjects.remove(project.name());
            try {
                boolean hasNodes = startStopExecutorsForProject(project);
                if (!hasNodes) {
                    cleanupProject(project.name());
                }
            } catch (Exception e) {
                log.error("failed startStopExecutorsForProject for project " + project.name(), e);
            }
        }
        for (String project : removedProjects) {
            try {
                stopNodes(project, executers.get(project));
                cleanupProject(project);
                log.info("removed project " + project);
            } catch (Exception e) {
                log.error("failed to stop nodes for project " + project, e);
            }
        }
    }

    /**
     * assuming nodes already stopped
     */
    private void cleanupProject(String project) {
        log.info("cleanupProject " + project);
        executers.remove(project);
        peerStatus.removeProject(project);
    }

    private void stop(PeriodicExecuter e) {
        log.info("stopping 1executor " + e.name());
        e.stopWhenPossible();
    }

    private boolean startStopExecutorsForProject(ProjectJson project) {
        Map<NodeInfo, PeriodicExecuter> currentNodes = getCurrentNodes(project);
        log.info(
            "project: " + project.name() + " currentProjectExecutors: " + currentNodes.keySet());
        SelectedNodes selectedNodes;
        try {
            selectedNodes = new NodesSelector(currentNodes, getNodes(project)).selectStartStop();
        } catch (Exception e) {
            log.error(
                "failed to select nodes for project " + project.name() + " will leave old nodes "
                    + currentNodes, e);
            return !currentNodes.isEmpty();
        }
        log.info("selectedNodes: " + selectedNodes);
        stopNodes(project.name(), selectedNodes.nodesToStop());
        Map<NodeInfo, PeriodicExecuter> newProjectExecutors = selectedNodes
            .existingProjectExecutors();
        for (NodeInfo nodeJson : selectedNodes.nodesToStart()) {
            log.info("start exec1 monitoring node " + nodeJson + " in project " + project.name());
            try {
                PeriodicExecuter e = startExecuter(project, nodeJson);
                newProjectExecutors.put(nodeJson, e);
            } catch (Exception e1) {
                log.error("failed to start executor for node " + nodeJson + " in project " + project
                    .name(), e1);
            }
        }
        executers.put(project.name(), newProjectExecutors);
        log.info(
            "project: " + project.name() + " newProjectExecutors: " + newProjectExecutors.keySet());
        return !executers.get(project.name()).isEmpty();
    }

    private void stopNodes(String project, Map<NodeInfo, PeriodicExecuter> map) {
        for (Entry<NodeInfo, PeriodicExecuter> e : map.entrySet()) {
            log.info("stop exec1 monitoring node " + e.getKey() + " in project " + project);
            peerStatus.removeNode(project, e.getKey().name());
            stop(e.getValue());
        }
    }

    private Map<NodeInfo, PeriodicExecuter> getCurrentNodes(ProjectJson project) {
        Map<NodeInfo, PeriodicExecuter> currentNodes = executers.get(project.name());
        if (null == currentNodes) {
            currentNodes = Maps.newHashMap();
            executers.put(project.name(), currentNodes);
        }
        return currentNodes;
    }

    private PeriodicExecuter startExecuter(ProjectJson project, NodeInfo nodeJson) {
        log.info("Starting monitor thread for project " + project.name() + " node " + nodeJson);
        Task task;
        CollectorsRunner collectorsTask = collectorsRunnerFactory
            .create(project.name(), nodeJson);
        collectorsTask.init();
        task = collectorsTask;
        PeriodicExecuter periodicExecuter = new PeriodicExecuter(NODE_MONITOR_INTERVAL,
            task, "RunMonitors_" + project.name() + "_" + nodeJson.name());
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
