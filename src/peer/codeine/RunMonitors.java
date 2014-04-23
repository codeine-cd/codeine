package codeine;

import static com.google.common.collect.Maps.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import codeine.api.MonitorStatusInfo;
import codeine.api.NodeInfo;
import codeine.api.NodeWithMonitorsInfo;
import codeine.configuration.IConfigurationManager;
import codeine.configuration.NodeMonitor;
import codeine.configuration.PathHelper;
import codeine.credentials.CredentialsHelper;
import codeine.executer.Task;
import codeine.jsons.nodes.NodeDiscoveryStrategy;
import codeine.jsons.peer_status.PeerStatus;
import codeine.jsons.project.OperatingSystem;
import codeine.jsons.project.ProjectJson;
import codeine.mail.MailSender;
import codeine.mail.NotificationDeliverToMongo;
import codeine.model.Constants;
import codeine.model.Result;
import codeine.utils.ExceptionUtils;
import codeine.utils.FilesUtils;
import codeine.utils.StringUtils;
import codeine.utils.network.HttpUtils;
import codeine.utils.os_process.ProcessExecuter.ProcessExecuterBuilder;
import codeine.utils.os_process.ShellScript;
import codeine.utils.os_process.ShellScriptWithOutput;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class RunMonitors implements Task {
	private IConfigurationManager configurationManager;
	private String projectName;
	private static final Logger log = Logger.getLogger(RunMonitors.class);
	private Map<String, Long> lastRun = newHashMap();
	private PeerStatus projectStatusUpdater;
	private final MailSender mailSender;
	private final PathHelper pathHelper;
	private NodeInfo node;
	private NotificationDeliverToMongo notificationDeliverToMongo;
	private PeerStatusChangedUpdater mongoPeerStatusUpdater;
	private SnoozeKeeper snoozeKeeper;
	private ShellScript shellScript;

	public RunMonitors(IConfigurationManager configurationManager, String project, PeerStatus projectStatusUpdater, MailSender mailSender,
			PathHelper pathHelper, NodeInfo node, NotificationDeliverToMongo notificationDeliverToMongo,
			PeerStatusChangedUpdater mongoPeerStatusUpdater, SnoozeKeeper snoozeKeeper) {
		this.configurationManager = configurationManager;
		this.projectName = project;
		this.projectStatusUpdater = projectStatusUpdater;
		this.mailSender = mailSender;
		this.pathHelper = pathHelper;
		this.node = node;
		this.notificationDeliverToMongo = notificationDeliverToMongo;
		this.mongoPeerStatusUpdater = mongoPeerStatusUpdater;
		this.snoozeKeeper = snoozeKeeper;
		init();
	}

	private void init() {
		String monitorOutputDirWithNode = pathHelper.getMonitorOutputDirWithNode(project().name(), node.name());
		FilesUtils.mkdirs(monitorOutputDirWithNode);
	}

	private ProjectJson project() {
		return configurationManager.getProjectForName(projectName);
	}

	@Override
	public void run() {
		List<NodeMonitor> monitors = Lists.newArrayList(project().monitors());
		removeNonExistMonitors();
		for (NodeMonitor monitor : monitors) {
			try {
				shellScript = null;
				runMonitorOnce(monitor);
			} finally {
				if (null != shellScript){
					shellScript.delete();
				}
			}
		}
		updateVersion();
		if (project().node_discovery_startegy() == NodeDiscoveryStrategy.Script) {
			updateTagsByScript();
		}
		else if (project().node_discovery_startegy() == NodeDiscoveryStrategy.Configuration){
			updateTagsByConfiguration();
		}
	}

	private void updateTagsByConfiguration() {
		projectStatusUpdater.updateTags(project(), node.name(), node.alias(), node.tags());
	}

	private void removeNonExistMonitors() {
		boolean removed = projectStatusUpdater.removeNonExistMonitors(project(), node.name(), node.alias());
		if (removed) {
			updateStatusInMongo();
		}
	}

	@SuppressWarnings("serial")
	private void updateTagsByScript() {
		if (project().node_discovery_startegy() != NodeDiscoveryStrategy.Script || StringUtils.isEmpty(project().tags_discovery_script())) {
			log.info("tags discovery is not configured for project " + projectName);
			return;
		}
		Map<String, String> env = Maps.newHashMap();
		env.put(Constants.EXECUTION_ENV_NODE_NAME, node.name());
		env.put(Constants.EXECUTION_ENV_NODE_ALIAS, node.alias());
		env.put(Constants.EXECUTION_ENV_NODE_TAGS, StringUtils.collectionToString(projectStatusUpdater.getTags(project().name(), node.name()), ";"));
		env.put(Constants.EXECUTION_ENV_PROJECT_NAME, project().name());
		ShellScriptWithOutput script = new ShellScriptWithOutput(
				"tags_" + projectName + "_" + node.name(), project().tags_discovery_script(), pathHelper.getProjectDir(projectName), env);
		String tags = script.execute();
		if (tags.isEmpty()){
			tags = "[]";
		}
		List<String> tagsList = new Gson().fromJson(tags, new TypeToken<List<String>>(){}.getType());
		List<String> prevTags = projectStatusUpdater.updateTags(project(), node.name(), node.alias(), tagsList);
		if (!tagsList.equals(prevTags)) {
			updateStatusInMongo();
		}
	}
	
	private void updateVersion() {
		if (StringUtils.isEmpty(project().version_detection_script())) {
			log.info("version is not configured for project " + projectName);
			return;
		}
		Map<String, String> env = Maps.newHashMap();
		env.put(Constants.EXECUTION_ENV_NODE_NAME, node.name());
		env.put(Constants.EXECUTION_ENV_NODE_ALIAS, node.alias());
		env.put(Constants.EXECUTION_ENV_NODE_TAGS, StringUtils.collectionToString(projectStatusUpdater.getTags(project().name(), node.name()), ";"));
		env.put(Constants.EXECUTION_ENV_PROJECT_NAME, project().name());
		ShellScriptWithOutput script = new ShellScriptWithOutput(
				"version_" + projectName + "_" + node.name(), project().version_detection_script(), pathHelper.getProjectDir(projectName), env);
		String version = script.execute();
		if (version.isEmpty()){
			version = Constants.NO_VERSION;
		}
		String prevVersion = projectStatusUpdater.updateVersion(project(), node.name(), node.alias(), version);
		if (!version.equals(prevVersion)) {
			updateStatusInMongo();
		}
	}

	private void runMonitorOnce(NodeMonitor monitor) {
		Long lastRuntime = lastRun.get(monitor.name());
		if (lastRuntime == null || System.currentTimeMillis() - lastRuntime > minInterval(monitor)) {
			try {
				runMonitor(monitor);
			} catch (Exception e) {
				log.warn("got exception when executing monitor ", e);
			}
			lastRun.put(monitor.name(), System.currentTimeMillis());
		} else {
			log.info("skipping monitor " + monitor);
		}
	}

	private int minInterval(NodeMonitor c) {
		if (null == c.minInterval()) {
			return 20000;
		}
		return c.minInterval() * 60000;
	}

	private void runMonitor(NodeMonitor monitor) {
		Result res = null;
		Stopwatch stopwatch = Stopwatch.createStarted();
		try {
			boolean hasCredentials = hasCredentials(monitor);
			List<String> cmd = buildCmd(monitor, hasCredentials);
			log.info("will execute " + cmd);
			log.info("will execute encoded " + cmd);
			Map<String, String> map = Maps.newHashMap();
			map.put(Constants.EXECUTION_ENV_NODE_NAME, node.name());
			map.put(Constants.EXECUTION_ENV_NODE_ALIAS, node.alias());
			map.put(Constants.EXECUTION_ENV_PROJECT_NAME, projectName);
			map.put(Constants.EXECUTION_ENV_NODE_TAGS, StringUtils.collectionToString(projectStatusUpdater.getTags(project().name(), node.name()), ";"));
			res = new ProcessExecuterBuilder(cmd, pathHelper.getProjectDir(project().name())).env(map).build().execute();
		} catch (Exception e) {
			res = new Result(Constants.ERROR_MONITOR, e.getMessage());
			log.debug("error in monitor", e);
		}
		stopwatch.stop();
		// long millis = stopwatch.elapsed(TimeUnit.MILLISECONDS);
		if (null == res.output) {
			res.output = "No Output\n";
		}
		writeResult(res, monitor, stopwatch);
		String result = String.valueOf(res.success());
		MonitorStatusInfo monitorInfo = new MonitorStatusInfo(monitor.name(), result);
		String previousResult = updateStatusInDatastore(monitorInfo);
		log.info(monitor.name() + " ended with result: " + res.success() + " , previous result " + previousResult + ", took: " + stopwatch);
		if (shouldSendStatusToMongo(result, previousResult)) {
			updateStatusInMongo();
		}
		if (monitor.notification_enabled()) {
			if (Constants.IS_MAIL_STARTEGY_MONGO) {
				if (shouldSendNotificationToMongo(res, previousResult)) {
					notificationDeliverToMongo.sendCollectorResult(monitor.name(), node, project(), res.output);
				}
			} else {
				if (null == previousResult) {
					previousResult = result;
				}
				mailSender.sendMailIfNeeded(Boolean.valueOf(result), Boolean.valueOf(previousResult), monitor, node,
						res.output, project());
			}
		} else {
			log.debug("notification not enabled for " + monitor);
		}

	}

	private void updateStatusInMongo() {
		mongoPeerStatusUpdater.pushUpdate();
	}

	private boolean shouldSendNotificationToMongo(Result res, String previousResult) {
		if (snoozeKeeper.isSnooze(project().name(), node.name())) {
			log.info("in snooze period");
			return false;
		}
		return null != previousResult && Boolean.valueOf(previousResult) && !res.success();
	}

	private boolean shouldSendStatusToMongo(String result, String previousResult) {
		return !result.equals(previousResult);
	}

	protected boolean hasCredentials(NodeMonitor collector) {
		return collector.credentials() != null;
	}

	private String updateStatusInDatastore(MonitorStatusInfo monitor) {
		return projectStatusUpdater.updateStatus(project(), monitor, node.name(), node.alias());
	}

	private List<String> buildCmd(NodeMonitor c, boolean hasCredentials) {
		String fileName = pathHelper.getMonitorsDir(project().name()) + File.separator + c.name();
		if (c.script_content() != null) {
			if (null != shellScript){
				log.warn("'shellScript' should be null but not", new RuntimeException());
			}
			fileName += node.name();
			shellScript = new ShellScript(fileName, c.script_content(), project().operating_system() == OperatingSystem.Windows, null);
			fileName = shellScript.create();
			log.info("file is " + fileName);
		}
		else if (FilesUtils.exists(fileName)){ //TODO remove after build 1100
			log.warn("monitor is in old format " + fileName);
		}
		else {
			throw new RuntimeException("monitor is missing " + fileName);
		}
		List<String> cmd = new ArrayList<String>();
		if (project().operating_system() == OperatingSystem.Windows){
			cmd.add(fileName);
		}
		else if (hasCredentials) {
			cmd.add(PathHelper.getReadLogs());
			cmd.add(encode(c.credentials()));
			cmd.add(encode("/bin/sh"));
			cmd.add(encode("-xe"));
			cmd.add(encode(fileName));
		} else {
			cmd.add("/bin/sh");
			cmd.add("-xe");
			cmd.add(fileName);
		}
		return cmd;
	}

	private String encode(final String value1) {
		return new CredentialsHelper().encode(value1);
	}

	private void writeResult(Result res, NodeMonitor collector, Stopwatch stopwatch) {
		String file = pathHelper.getMonitorOutputDirWithNode(project().name(), node.name()) + "/" + HttpUtils.specialEncode(collector.name())
				+ ".txt";
		log.debug("Output for " + collector.name() + " will be written to: " + file);
		NodeWithMonitorsInfo nodeInfo = projectStatusUpdater.nodeInfo(project(), node.name(), node.alias());
		try (BufferedWriter out = new BufferedWriter(new FileWriter(file));) {
			out.write("+------------------------------------------------------------------+\n");
			out.write("| monitor:       " + collector.name() + "\n");
			if (hasCredentials(collector)) {
			out.write("| credentials:   " + collector.credentials() + "\n");
			}
			out.write("| exitstatus:    " + res.exit() + "\n");
			out.write("| completed at:  " + new Date() + "\n");
			out.write("| length:        " + stopwatch + "\n");
			out.write("| project:       " + project().name() + "\n");
			out.write("| node:          " + node.alias() + "\n");
			out.write("| node-name:     " + node.name() + "\n");
			out.write("| version:       " + nodeInfo.version() + "\n");
			out.write("+------------------------------------------------------------------+\n");
			out.write(res.output);
		} catch (IOException e) {
			throw ExceptionUtils.asUnchecked(e);
		}
	}

	@Override
	public String toString() {
		return "RunMonitors [project=" + project() + "]";
	}

}
