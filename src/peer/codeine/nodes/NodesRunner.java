package codeine.nodes;

import codeine.api.NodeInfo;
import codeine.collectors.CollectorsRunner;
import codeine.collectors.CollectorsRunnerFactory;
import codeine.configuration.IConfigurationManager;
import codeine.executer.PeriodicExecuter;
import codeine.executer.Task;
import codeine.jsons.global.GlobalConfigurationJson;
import codeine.jsons.nodes.NodesManager;
import codeine.jsons.peer_status.PeerStatus;
import codeine.jsons.project.ProjectJson;
import codeine.utils.network.InetUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.apache.log4j.Logger;

public class NodesRunner implements Task {

    private static final Logger log = Logger.getLogger(NodesRunner.class);

    private long nodeMonitorInterval = TimeUnit.SECONDS.toMillis(60);

    @Inject
    private GlobalConfigurationJson globalConfigurationJson;

    @Inject
    private IConfigurationManager configurationManager;

    @Inject
    private PeerStatus peerStatus;

    @Inject
    private NodesManager nodesManager;

    @Inject
    private CollectorsRunnerFactory collectorsRunnerFactory;

    private Map<String, Map<NodeInfo, PeriodicExecuter>> executers = Maps.newHashMap();

    @Override
    public synchronized void run() {
        nodeMonitorInterval = TimeUnit.SECONDS.toMillis(globalConfigurationJson.node_interval_seconds());
        InetAddress localHost = InetUtils.getLocalHost();
        log.info("NodeRunner is starting on host " + localHost.getHostName() + " " + localHost.getCanonicalHostName());
        log.info("NodeRunner is starting " + this + " with executers " + executers + " interval is "
            + nodeMonitorInterval);
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
        log.info("project: " + project.name() + " currentProjectExecutors: " + currentNodes.keySet());
        SelectedNodes selectedNodes;
        try {
            selectedNodes = new NodesSelector(currentNodes, getNodes(project)).selectStartStop();
        } catch (Exception e) {
            log.error("failed to select nodes for project " + project.name() + " will leave old nodes " + currentNodes,
                e);
            return !currentNodes.isEmpty();
        }
        log.info("selectedNodes: " + selectedNodes);
        stopNodes(project.name(), selectedNodes.nodesToStop());
        Map<NodeInfo, PeriodicExecuter> newProjectExecutors = selectedNodes.existingProjectExecutors();
        for (NodeInfo nodeJson : selectedNodes.nodesToStart()) {
            log.info("start exec1 monitoring node " + nodeJson + " in project " + project.name());
            try {
                PeriodicExecuter e = startExecuter(project, nodeJson);
                newProjectExecutors.put(nodeJson, e);
            } catch (Exception e1) {
                log.error("failed to start executor for node " + nodeJson + " in project " + project.name(), e1);
            }
        }
        executers.put(project.name(), newProjectExecutors);
        log.info("project: " + project.name() + " newProjectExecutors: " + newProjectExecutors.keySet());
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
        return executers.computeIfAbsent(project.name(), k -> Maps.newHashMap());
    }

    private PeriodicExecuter startExecuter(ProjectJson project, NodeInfo nodeJson) {
        log.info("Starting monitor thread for project " + project.name() + " node " + nodeJson);
        Task task;
        CollectorsRunner collectorsTask = collectorsRunnerFactory.create(project.name(), nodeJson);
        collectorsTask.init();
        task = collectorsTask;
        PeriodicExecuter periodicExecuter = new PeriodicExecuter(nodeMonitorInterval, task,
            "RunMonitors_" + project.name() + "_" + nodeJson.name());
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
