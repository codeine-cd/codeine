package codeine.collectors.tags;

import java.util.List;

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
import codeine.utils.logging.LogUtils;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.inject.assistedinject.Assisted;

public class TagsCollectorRunner implements IOneCollectorRunner {

	private static final Logger log = Logger.getLogger(TagsCollectorRunner.class);
	
	@Inject private PeerStatus projectStatusUpdater;
	@Inject private PeerStatusChangedUpdater peerStatusChangedUpdater;
	private IConfigurationManager configurationManager;
	private OneCollectorRunner runner;
	private ProjectJson project;
	private String projectName;
	private NodeInfo node;

	@Inject 
	public TagsCollectorRunner(@Assisted String projectName, @Assisted NodeInfo node
			, IConfigurationManager configurationManager, OneCollectorRunnerFactory oneCollectorRunnerFactory) {
		super();
		this.projectName = projectName;
		this.node = node;
		this.configurationManager = configurationManager;
		CollectorInfo collectorInfo = initAndGetConf();
		runner = oneCollectorRunnerFactory.create(collectorInfo, project, node);
	}

	private CollectorInfo initAndGetConf() {
		project = configurationManager.getProjectForName(projectName);
		CollectorInfo collectorInfo = new CollectorInfo(Constants.TAGS_COLLECTOR_NAME, project.tags_discovery_script(), CollectorType.String);
		return collectorInfo;
	}

	@Override
	public void execute() {
		CollectorInfo collectorInfo = initAndGetConf();
		runner.updateConf(collectorInfo);
		List<String> tags = getTagsList();
		updateTags(tags);
	}

	public void updateTags(List<String> tags) {
		List<String> prevTags = projectStatusUpdater.updateTags(project, node.name(), node.alias(), tags);
		if (!tags.equals(prevTags)) {
			LogUtils.info(log, "tags should update", tags, prevTags);
			peerStatusChangedUpdater.pushUpdate("TagsCollectorRunner.updateTags()");
		}
	}

	public List<String> getTagsList() {
		switch (project.node_discovery_startegy()) {
		case Script:
			if (project.tags_discovery_script() == null) {
				return Lists.newArrayList();
			}
			return getTagsByScript();
		case Configuration:
			return getTagsByConfiguration();
		default:
			LogUtils.assertFailed(log, "could not find how to execute strategy " + project.node_discovery_startegy() + " for project " + project.name());
			return Lists.newArrayList();
		}
	}
	
	private List<String> getTagsByScript() {
		runner.execute();
		String tags = runner.outputFromFile();
		log.info("output is " + tags);
		if (tags.isEmpty()){
			tags = "[]";
		}
		@SuppressWarnings("serial")
		List<String> tagsList = new Gson().fromJson(tags, new TypeToken<List<String>>(){}.getType());
		return tagsList;
	}

	private List<String> getTagsByConfiguration() {
		return node.tags();
	}

}
