package codeine.collectors;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.PeerStatusChangedUpdater;
import codeine.api.NodeInfo;
import codeine.configuration.IConfigurationManager;
import codeine.configuration.NodeMonitor;
import codeine.jsons.collectors.CollectorInfo;
import codeine.jsons.collectors.CollectorInfo.CollectorType;
import codeine.jsons.peer_status.PeerStatus;
import codeine.jsons.project.ProjectJson;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.assistedinject.Assisted;

public class CollectorsListHolder {

	private static final Logger log = Logger.getLogger(CollectorsListHolder.class);
	
	@Inject private PeerStatus peerStatus;
	@Inject private IConfigurationManager configurationManager;
	@Inject private PeerStatusChangedUpdater peerStatusChangedUpdater;
	@Inject private OneCollectorRunnerFactory oneCollectorRunnerFactory;
	private String projectName;
	private NodeInfo node;
	private Map<String, OneCollectorRunner> runnersMap = Maps.newLinkedHashMap();
	
	@Inject
	public CollectorsListHolder(@Assisted String projectName, @Assisted NodeInfo node) {
		this.projectName = projectName;
		this.node = node;
	}

	public Collection<OneCollectorRunner> getCurrentListAndRemoveOldCollectors() {
		ProjectJson project = project();
		removeFromMap(project);
		addAndUpdateConfInMap(project);
		boolean removed = peerStatus.removeNonExistCollectors(project, node.name(), node.alias());
		if (removed) {
			peerStatusChangedUpdater.pushUpdate();
		}
		return runnersMap.values();
	}

	private void addAndUpdateConfInMap(ProjectJson project) {
		//TODO monitors backward
		for (NodeMonitor monitorInfo : project.monitors()) {
			String name = monitorInfo.name();
			CollectorType type = CollectorType.Monitor;
			int minInterval = monitorInfo.minInterval() == null ? 0 : monitorInfo.minInterval();
			CollectorInfo collectorInfo = new CollectorInfo(name, monitorInfo.script_content(), minInterval, monitorInfo.credentials(), type, monitorInfo.notification_enabled());
			if (runnersMap.containsKey(name)) {
				runnersMap.get(name).updateConf(collectorInfo);
			} else {
				runnersMap.put(name, oneCollectorRunnerFactory.create(collectorInfo, project, node));
			}
		}
		for (CollectorInfo collectorInfo : project.collectors()) {
			String name = collectorInfo.name();
			if (runnersMap.containsKey(name)) {
				runnersMap.get(name).updateConf(collectorInfo);
			} else {
				runnersMap.put(name, oneCollectorRunnerFactory.create(collectorInfo, project, node));
			}
		}
	}

	private void removeFromMap(ProjectJson project) {
		Set<String> names = Sets.newHashSet();
		for (CollectorInfo c : project.collectors()) {
			names.add(c.name());
		}
		//TODO monitors backward
		for (NodeMonitor m : project.monitors()) {
			names.add(m.name());
		}
		Set<String> namesToRemove = Sets.newHashSet(runnersMap.keySet());
		namesToRemove.removeAll(names);
		for (String name : namesToRemove) {
			log.info("removing collector " + name);
			runnersMap.remove(name);
		}
	}

	private ProjectJson project() {
		return configurationManager.getProjectForName(projectName);
	}
}
