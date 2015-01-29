package codeine.collectors.version;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.PeerStatusChangedUpdater;
import codeine.api.NodeInfo;
import codeine.collectors.IOneCollectorRunner;
import codeine.collectors.OneCollectorRunner;
import codeine.collectors.OneCollectorRunnerFactory;
import codeine.configuration.IConfigurationManager;
import codeine.jsons.collectors.CollectorInfo;
import codeine.jsons.collectors.CollectorInfo.CollectorType;
import codeine.jsons.peer_status.PeerStatus;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.utils.MiscUtils;
import codeine.utils.logging.LogUtils;

import com.google.inject.assistedinject.Assisted;

public class VersionCollectorRunner implements IOneCollectorRunner {

	private static final Logger log = Logger.getLogger(VersionCollectorRunner.class);
	
	@Inject private PeerStatus projectStatusUpdater;
	@Inject private PeerStatusChangedUpdater peerStatusChangedUpdater;
	private IConfigurationManager configurationManager;
	private OneCollectorRunner runner;
	private ProjectJson project;
	private String projectName;
	private NodeInfo node;

	@Inject 
	public VersionCollectorRunner(@Assisted String projectName, @Assisted NodeInfo node,
			OneCollectorRunnerFactory oneCollectorRunnerFactory, IConfigurationManager configurationManager) {
		super();
		this.projectName = projectName;
		this.node = node;
		this.configurationManager = configurationManager;
		CollectorInfo collectorInfo = initAndGetConf();
		runner = oneCollectorRunnerFactory.create(collectorInfo, project, node);
	}

	private CollectorInfo initAndGetConf() {
		project = configurationManager.getProjectForName(projectName);
		CollectorInfo collectorInfo = new CollectorInfo(Constants.VERSION_COLLECTOR_NAME, project.version_detection_script(), CollectorType.String);
		return collectorInfo;
	}

	@Override
	public void execute() {
		CollectorInfo collectorInfo = initAndGetConf();
		runner.updateConf(collectorInfo);
		runner.execute();
		String version = runner.outputFromFile();
		log.info("version is " + version);
		String prevVersion = projectStatusUpdater.updateVersion(project, node.name(), node.alias(), version);
		if (!MiscUtils.equals(version, prevVersion)) {
			LogUtils.info(log, "version should update", version, prevVersion);
			peerStatusChangedUpdater.pushUpdate("VersionCollectorRunner.execute()");
		}
	}

}
