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
import codeine.mail.NotificationDeliverToDatabase;
import codeine.model.Constants;
import codeine.model.ExitStatus;
import codeine.model.Result;
import codeine.utils.MiscUtils;
import codeine.utils.StringUtils;
import codeine.utils.TextFileUtils;
import codeine.utils.logging.LogUtils;
import codeine.utils.network.HttpUtils;
import codeine.utils.os_process.ShellScript;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.inject.assistedinject.Assisted;

public class OneCollectorRunner implements IOneCollectorRunner {

	private static final int MIN_INTERVAL_MILLI = 20000;
	private static final Logger log = Logger.getLogger(OneCollectorRunner.class);
	@Inject private PathHelper pathHelper;
	@Inject private PeerStatus peerStatus;
	@Inject private Gson gson;
	@Inject private PeerStatusChangedUpdater peerStatusChangedUpdater;
	@Inject private SnoozeKeeper snoozeKeeper;
	@Inject private NotificationDeliverToDatabase notificationDeliverToDatabase;
	private CollectorInfo collectorInfo;
	private ProjectJson project;
	private NodeInfo node;
	private Long lastRuntime;
	private Result result;
	private Result previousResult;
	private Stopwatch stopwatch;
	
	@Inject
	public OneCollectorRunner(@Assisted CollectorInfo collector, @Assisted ProjectJson project, @Assisted NodeInfo node) {
		this.collectorInfo = collector;
		this.project = project;
		this.node = node;
	}

	@Override
	public void execute() {
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
		stopwatch = Stopwatch.createStarted();
		executeScriptAndDeleteIt(shellScript);
		stopwatch.stop();
		if (null == result.output()) {
			result.output("No Output\n");
		}
		CollectorExecutionInfo info = new CollectorExecutionInfo(collectorInfo.name(), collectorInfo.type(), result.exit(), outputFromFile(), stopwatch.elapsed(TimeUnit.MILLISECONDS), startTime);
		CollectorExecutionInfoWithResult resultWrapped = new CollectorExecutionInfoWithResult(info, result);
		processResult(resultWrapped, stopwatch);
	}

	public String outputFromFile() {
		if (null == result) {
			return "";
		}
		return result.outputFromFile();
	}

	private void processResult(CollectorExecutionInfoWithResult resultWrapped, Stopwatch stopwatch) {
		resultWrapped.result().limitOutputLength();
		writeResult(resultWrapped);
		CollectorExecutionInfo lastValue = updateStatusInDataset(resultWrapped.info());
		log.info("collector '" + collectorInfo.name() + "' took:" + stopwatch + " result:" + resultWrapped.info().valueAndExitStatus() + (null != lastValue ? " previous:" + lastValue.valueAndExitStatus() : ""));
		updateDatastoreIfNeeded();
		sendNotificationIfNeeded();
	}

	private ShellScript createShellScript() {
		ShellScript shellScript = new ShellScript(getKey(), collectorInfo.script_content(), project.operating_system(), null, pathHelper.getProjectDir(project.name()), prepareEnv(), collectorInfo.cred());
		shellScript.create();
		return shellScript;
	}

	private String getKey() {
		return pathHelper.getMonitorsDir(project.name()) + File.separator + node.name() + "_" + collectorInfo.name();
	}

	private void executeScriptAndDeleteIt(ShellScript shellScript) {
		try {
			result = shellScript.execute();
		} catch (Exception ex) {
			result = new Result(ExitStatus.EXCEPTION, ex.getMessage());
			log.debug("error in collector", ex);
		} finally {
			shellScript.delete();
		}
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
		if (collectorInfo.notification_enabled() && shouldNotify()) {
			notificationDeliverToDatabase.sendCollectorResult(
					collectorInfo.name(), node, project, result.output(), result.exit(), stopwatch.toString(), true);
		}
		previousResult = result;
	}

	private boolean shouldNotify() {
		return null != previousResult && null != result 
				&& result.exit() != 0 && result.exit() != previousResult.exit();
	}

	private boolean shouldUpdate() {
		if (result == null) {
			return false;
		}
		if (previousResult == null) {
			return true;
		}
		boolean shouldUpdate = !MiscUtils.equals(result.outputFromFile(), previousResult.outputFromFile()) || !MiscUtils.equals(result.exit(), previousResult.exit());
		if (shouldUpdate) {
			LogUtils.info(log, "collector should update", result.outputFromFile(), previousResult.outputFromFile(), result.exit(), previousResult.exit());
		}
		return shouldUpdate;
	}
	
	private void updateDatastoreIfNeeded() {
		if (shouldUpdate()) {
			peerStatusChangedUpdater.pushUpdate();
		}
	}


	private CollectorExecutionInfo updateStatusInDataset(CollectorExecutionInfo info) {
		return peerStatus.updateStatus(project, info, node.name(), node.alias());
	}

	private void writeResult(CollectorExecutionInfoWithResult result) {
		String file = pathHelper.getCollectorOutputDirWithNode(project.name(), node.name()) + "/" + HttpUtils.specialEncode(collectorInfo.name())
				+ ".txt";
		log.debug("Output for " + collectorInfo.name() + " will be written to: " + file);
		TextFileUtils.setContents(file, gson.toJson(result));
	}

	private int minInterval() {
		if (collectorInfo.min_interval() == null || collectorInfo.min_interval() <= 0) {
			return MIN_INTERVAL_MILLI;
		}
		return collectorInfo.min_interval() * 60000;
	}

	public void updateConf(CollectorInfo collectorInfo) {
		this.collectorInfo = collectorInfo;
	}
}
