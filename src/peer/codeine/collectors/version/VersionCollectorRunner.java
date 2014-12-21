package codeine.collectors.version;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.PeerStatusChangedUpdater;
import codeine.api.NodeInfo;
import codeine.collectors.IOneCollectorRunner;
import codeine.collectors.OneCollectorRunner;
import codeine.collectors.OneCollectorRunnerFactory;
import codeine.jsons.collectors.CollectorInfo;
import codeine.jsons.collectors.CollectorInfo.CollectorType;
import codeine.jsons.peer_status.PeerStatus;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;

import com.google.inject.assistedinject.Assisted;

public class VersionCollectorRunner implements IOneCollectorRunner {

	private static final Logger log = Logger.getLogger(VersionCollectorRunner.class);
	
	@Inject private PeerStatus projectStatusUpdater;
	@Inject private PeerStatusChangedUpdater peerStatusChangedUpdater;
	private OneCollectorRunner runner;
	private ProjectJson project;
	private NodeInfo node;

	@Inject 
	public VersionCollectorRunner(@Assisted ProjectJson project, @Assisted NodeInfo node,
			OneCollectorRunnerFactory oneCollectorRunnerFactory) {
		super();
		this.project = project;
		this.node = node;
		CollectorInfo collectorInfo = new CollectorInfo(Constants.VERSION_COLLECTOR_NAME, project.version_detection_script(), CollectorType.String);
		runner = oneCollectorRunnerFactory.create(collectorInfo, project, node);
	}

	@Override
	public void execute() {
		runner.execute();
		String version = runner.outputFromFile();
		log.info("version is " + version);
		String prevVersion = projectStatusUpdater.updateVersion(project, node.name(), node.alias(), version);
		if (!version.equals(prevVersion)) {
			peerStatusChangedUpdater.pushUpdate();
		}
	}

}
