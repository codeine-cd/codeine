package codeine.collectors;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.PeerStatusChangedUpdater;
import codeine.SnoozeKeeper;
import codeine.api.NodeInfo;
import codeine.configuration.PathHelper;
import codeine.jsons.collectors.CollectorExecutionInfo;
import codeine.jsons.collectors.CollectorExecutionInfoWithResult;
import codeine.jsons.collectors.CollectorInfo;
import codeine.jsons.peer_status.PeerStatus;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.model.ExitStatus;
import codeine.model.Result;
import codeine.utils.MiscUtils;
import codeine.utils.StringUtils;
import codeine.utils.TextFileUtils;
import codeine.utils.network.HttpUtils;
import codeine.utils.os_process.ShellScript;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.inject.assistedinject.Assisted;

public class OneCollectorRunner {

	private static final Logger log = Logger.getLogger(OneCollectorRunner.class);
	@Inject private PathHelper pathHelper;
	@Inject private PeerStatus peerStatus;
	@Inject private Gson gson;
	@Inject private PeerStatusChangedUpdater peerStatusChangedUpdater;
	@Inject private SnoozeKeeper snoozeKeeper;
	private CollectorInfo collectorInfo;
	private ProjectJson project;
	private NodeInfo node;
	private Long lastRuntime;
	
	@Inject
	public OneCollectorRunner(@Assisted CollectorInfo collector, @Assisted ProjectJson project, @Assisted NodeInfo node) {
		this.collectorInfo = collector;
		this.project = project;
		this.node = node;
	}

	public void run() {
		runOnceCheckMinInterval();
	}

	private void runOnceCheckMinInterval() {
		if (lastRuntime == null || System.currentTimeMillis() - lastRuntime > minInterval()) {
			try {
				runOnce();
			} catch (Exception e) {
				log.warn("got exception when executing collector ", e);
			}
			lastRuntime = System.currentTimeMillis();
		} else {
			log.info("skipping collector " + collectorInfo);
		}
	}

	private void runOnce() {
		ShellScript shellScript = createShellScript();
		long startTime = System.currentTimeMillis();
		Stopwatch stopwatch = Stopwatch.createStarted();
		Result result = executeScriptAndDeleteIt(shellScript);
		stopwatch.stop();
		if (null == result.output()) {
			result.output("No Output\n");
		}
		CollectorExecutionInfo info = new CollectorExecutionInfo(collectorInfo.name(), collectorInfo.type(), result.exit(), result.outputFromFile(), stopwatch.elapsed(TimeUnit.MILLISECONDS), startTime);
		CollectorExecutionInfoWithResult resultWrapped = new CollectorExecutionInfoWithResult(info, result);
		processResult(resultWrapped, stopwatch);
	}

	private void processResult(CollectorExecutionInfoWithResult resultWrapped, Stopwatch stopwatch) {
		resultWrapped.result().limitOutputLength();
		writeResult(resultWrapped);
		String lastValue = updateStatusInDataset(resultWrapped.info());
		log.info("collector " + collectorInfo.name() + " ended with value: " + resultWrapped.info().value() + " , previous result " + lastValue + ", took: " + stopwatch);
		updateDatastoreIfNeeded(lastValue, resultWrapped.result().outputFromFile());
		sendNotificationIfNeeded();
	}

	private ShellScript createShellScript() {
		ShellScript shellScript = new ShellScript(getKey(), collectorInfo.script_content(), project.operating_system(), null, pathHelper.getProjectDir(project.name()), prepareEnv(), collectorInfo.credentials());
		shellScript.create();
		return shellScript;
	}

	private String getKey() {
		return pathHelper.getMonitorsDir(project.name()) + File.separator + node.name() + "_" + collectorInfo.name();
	}

	private Result executeScriptAndDeleteIt(ShellScript shellScript) {
		Result result;
		try {
			result = shellScript.execute();
		} catch (Exception ex) {
			result = new Result(ExitStatus.EXCEPTION, ex.getMessage());
			log.debug("error in collector", ex);
		} finally {
			shellScript.delete();
		}
		return result;
	}

	private Map<String, String> prepareEnv() {
		Map<String, String> env = Maps.newHashMap();
		env.put(Constants.EXECUTION_ENV_NODE_NAME, node.name());
		env.put(Constants.EXECUTION_ENV_NODE_ALIAS, node.alias());
		env.put(Constants.EXECUTION_ENV_PROJECT_NAME, project.name());
		env.put(Constants.EXECUTION_ENV_NODE_TAGS, StringUtils.collectionToString(peerStatus.getTags(project.name(), node.name()), ";"));
		env.putAll(project.environmentVariables());
		return env;
	}

	private void sendNotificationIfNeeded() {
		if (snoozeKeeper.isSnooze(project.name(), node.name())) {
			log.info("in snooze period");
			return;
		}
//		return null != previousResult && Boolean.valueOf(previousResult) && !res.success();
//		collectorInfo.notification_enabled();
		//TODO decide when to notify somehow
	}

	private void updateDatastoreIfNeeded(String lastValue, String currentValue) {
		log.info("last value " + lastValue + " current value " + currentValue);
		if (!MiscUtils.equals(lastValue, currentValue)) {
			peerStatusChangedUpdater.pushUpdate();
		}
	}

	private String updateStatusInDataset(CollectorExecutionInfo info) {
		String lastValue = peerStatus.updateStatus(project, info, node.name(), node.alias());
		return lastValue;
	}

	private void writeResult(CollectorExecutionInfoWithResult result) {
		String file = pathHelper.getMonitorOutputDirWithNode(project.name(), node.name()) + "/" + HttpUtils.specialEncode(collectorInfo.name())
				+ ".txt";
		log.debug("Output for " + collectorInfo.name() + " will be written to: " + file);
		TextFileUtils.setContents(file, gson.toJson(result));
	}

	private int minInterval() {
		if (0 == collectorInfo.min_interval()) {
			return 20000;
		}
		return collectorInfo.min_interval() * 60000;
	}

	public void updateConf(CollectorInfo collectorInfo) {
		this.collectorInfo = collectorInfo;
	}
}
