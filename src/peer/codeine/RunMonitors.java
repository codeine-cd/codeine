package codeine;

import static com.google.common.collect.Maps.newHashMap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import codeine.api.MonitorInfo;
import codeine.api.NodeInfo;
import codeine.configuration.CollectorRule;
import codeine.configuration.HttpCollector;
import codeine.configuration.PathHelper;
import codeine.configuration.VersionCollector;
import codeine.credentials.CredentialsHelper;
import codeine.executer.Task;
import codeine.jsons.peer_status.PeerStatus;
import codeine.jsons.project.ProjectJson;
import codeine.mail.MailSender;
import codeine.mail.NotificationDeliverToMongo;
import codeine.model.Constants;
import codeine.model.Result;
import codeine.utils.ExceptionUtils;
import codeine.utils.FilesUtils;
import codeine.utils.os_process.ProcessExecuter.ProcessExecuterBuilder;

import com.google.common.base.Stopwatch;

public class RunMonitors implements Task {
	private ProjectJson project;
	private static final Logger log = Logger.getLogger(RunMonitors.class);
	private Map<String, Long> lastRun = newHashMap();
	private PeerStatus projectStatusUpdater;
	private final MailSender mailSender;
	private final PathHelper pathHelper;
	private NodeInfo node;
	private NotificationDeliverToMongo notificationDeliverToMongo;
	private PeerStatusChangedUpdater mongoPeerStatusUpdater;
	private SnoozeKeeper snoozeKeeper;

	public RunMonitors(ProjectJson project, PeerStatus projectStatusUpdater, MailSender mailSender,
			PathHelper pathHelper, NodeInfo node, NotificationDeliverToMongo notificationDeliverToMongo,
			PeerStatusChangedUpdater mongoPeerStatusUpdater, SnoozeKeeper snoozeKeeper) {
		this.project = project;
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
		String monitorOutputDirWithNode = pathHelper.getMonitorOutputDirWithNode(project.name(), node.name());
		FilesUtils.mkdirs(monitorOutputDirWithNode);
	}

	@Override
	public void run() {
		List<File> monitors = pathHelper.getMonitors(project.name());
		for (File monitor : monitors) {
			HttpCollector c = project.getCollector(monitor.getName());
			if (null == c) {
				if (monitor.getName().equals(new VersionCollector().name())) {
					c = new VersionCollector();
				}
			}
			runMonitorOnce(monitor, c);
		}
	}

	private void runMonitorOnce(File monitor, HttpCollector c) {
		if (null == c) {
			log.debug("no collector defined for monitor file " + monitor.getName() + ", skipping exectution");
			return;
		}
		Long lastRuntime = lastRun.get(c.name());
		if (lastRuntime == null || System.currentTimeMillis() - lastRuntime > minInterval(c)) {
			try {
				runMonitor(monitor, c);
			} catch (Exception e) {
				log.warn("got exception when executing monitor ", e);
			}
			lastRun.put(c.name(), System.currentTimeMillis());
		} else {
			log.info("skipping monitor " + monitor);
		}
	}

	private int minInterval(HttpCollector c) {
		if (null == c.minInterval()) {
			return 20000;
		}
		return c.minInterval() * 60000;
	}

	private void runMonitor(File monitor, HttpCollector collector) {
		boolean hasCredentials = hasCredentials(collector);
		List<String> cmd = buildCmd(monitor, collector, hasCredentials);
		List<String> cmdForOutput = hasCredentials ? buildCmd(monitor, collector, false) : cmd;
		log.info("will execute " + cmdForOutput);
		log.info("will execute encoded " + cmd);
		Stopwatch stopwatch = new Stopwatch().start();
		Result res = null;
		try {
			res = new ProcessExecuterBuilder(cmd, monitor.getParent()).cmdForOutput(cmdForOutput).build().execute();
		} catch (Exception e) {
			if (monitor.getName().equals("version")) {
				res = new Result(Constants.ERROR_MONITOR, "");
			}
			else {
				res = new Result(Constants.ERROR_MONITOR, e.getMessage());
			}
			log.debug("error in monitor", e);
		}
		stopwatch.stop();
		// long millis = stopwatch.elapsed(TimeUnit.MILLISECONDS);
		log.info(monitor.getName() + " ended with result: " + res.success() + ", took: " + stopwatch);
		writeResult(res, monitor.getName(), collector, stopwatch, cmdForOutput);
		String result = isVersionCollector(collector) ? res.output.trim() : String.valueOf(res.success());
		MonitorInfo monitorInfo = new MonitorInfo(monitor.getName(), monitor.getName(), result);
		String previousResult = updateStatusInDatastore(monitorInfo);
		if (shouldSendStatusToMongo(res, previousResult)) {
			updateStatusInMongo();
		}
		if (collector.notification_enabled()) {
			if (Constants.IS_MAIL_STARTEGY_MONGO) {
				if (shouldSendNotificationToMongo(res, previousResult)) {
					notificationDeliverToMongo.sendCollectorResult(collector.name(), node, project, res.output);
				}
			} else {
				if (null == previousResult) {
					previousResult = result;
				}
				mailSender.sendMailIfNeeded(Boolean.valueOf(result), Boolean.valueOf(previousResult), collector, node,
						res.output, project);
			}
		} else {
			log.debug("notification not enabled for " + collector);
		}

	}

	private void updateStatusInMongo() {
		mongoPeerStatusUpdater.pushUpdate();
	}

	private boolean shouldSendNotificationToMongo(Result res, String previousResult) {
		if (snoozeKeeper.isSnooze(project.name(), node.name())) {
			return false;
		}
		return null != previousResult && Boolean.valueOf(previousResult) && !res.success();
	}

	private boolean shouldSendStatusToMongo(Result res, String previousResult) {
		return Boolean.valueOf(previousResult) != res.success();
	}

	protected boolean hasCredentials(HttpCollector collector) {
		return collector.credentials() != null;
	}

	private boolean isVersionCollector(HttpCollector c) {
		return c instanceof VersionCollector;
	}

	private String updateStatusInDatastore(MonitorInfo monitor) {
		return projectStatusUpdater.updateStatus(project, monitor, node.name(), node.alias());
	}

	private List<String> buildCmd(File monitor, HttpCollector c, boolean hasCredentials) {
		List<String> cmd = new ArrayList<String>();
		if (hasCredentials) {
			cmd.add(PathHelper.getReadLogs());
			cmd.add(encode(c.credentials()));
			cmd.add(encode(monitor.getAbsolutePath()));
		} else {
			cmd.add(monitor.getAbsolutePath());
		}
		for (CollectorRule r : c.rule()) {
			if (r.shouldApplyForNode(node.name())) {
				for (String arg : r.arg()) {
					if (hasCredentials) {
						cmd.add(encode(replace(arg)));
					} else {
						cmd.add(replace(arg));
					}
				}
			}
		}
		return cmd;
	}

	private String replace(String arg) {
		return arg.replace(Constants.REPLACE_NODE_NAME, node.name());
	}

	private String encode(final String value1) {
		return new CredentialsHelper().encode(value1);
	}

	private void writeResult(Result res, String outputFileName, HttpCollector collector, Stopwatch stopwatch,
			List<String> cmd) {
		String file = pathHelper.getMonitorOutputDirWithNode(project.name(), node.name()) + "/" + outputFileName
				+ ".txt";
		log.debug("Output for " + outputFileName + " will be written to: " + file);

		try (BufferedWriter out = new BufferedWriter(new FileWriter(file));) {
			out.write("+------------------------------------------------------------------+\n");
			out.write("| command: " + cmd + "\n");
			if (hasCredentials(collector)) {
				out.write("| credentials: " + collector.credentials() + "\n");
			}
			out.write("| exitstatus: " + res.exit() + "\n");
			out.write("| completed at: " + new Date() + "\n");
			out.write("| length: " + stopwatch + "\n");
			out.write("| project: " + project.name() + "\n");
			out.write("| node: " + node.alias() + "\n");
			out.write("+------------------------------------------------------------------+\n");
			out.write(res.output);
		} catch (IOException e) {
			throw ExceptionUtils.asUnchecked(e);
		}
	}

	@Override
	public String toString() {
		return "RunMonitors [project=" + project + "]";
	}

}
