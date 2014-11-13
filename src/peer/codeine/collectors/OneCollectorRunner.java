package codeine.collectors;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.api.NodeInfo;
import codeine.configuration.PathHelper;
import codeine.jsons.collectors.CollectorExecutionInfo;
import codeine.jsons.collectors.CollectorExecutionInfoWithResult;
import codeine.jsons.collectors.CollectorInfo;
import codeine.jsons.peer_status.PeerStatus;
import codeine.jsons.project.ProjectJson;
import codeine.model.Result;
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
		long startTime = System.currentTimeMillis();
		Stopwatch stopwatch = Stopwatch.createStarted();
		String key = pathHelper.getMonitorsDir(project.name()) + File.separator + node.name() + "_" + collectorInfo.name();
		Map<String, String> env = Maps.newHashMap();
		ShellScript shellScript = new ShellScript(key, collectorInfo.script_content(), project.operating_system(), null, pathHelper.getProjectDir(project.name()), env);
//		String fileName = 
				shellScript.create();
		//TODO credentials
		//TODO populate env variables
		Result result = shellScript.execute();
		stopwatch.stop();
		if (null == result.output()) {
			result.output("No Output\n");
		}
		CollectorExecutionInfo info = new CollectorExecutionInfo(collectorInfo.name(), collectorInfo.type(), result.exit(), result.outputFromFile(), stopwatch.elapsed(TimeUnit.MILLISECONDS), startTime);
		writeResult(new CollectorExecutionInfoWithResult(info, result));
		String lastValue = updateStatusInDataset(info);
		updateDatastoreIfNeeded();
		sendNotificationIfNeeded();
	}

	private void sendNotificationIfNeeded() {
		// TODO Auto-generated method stub
		
	}

	private void updateDatastoreIfNeeded() {
		// TODO Auto-generated method stub
		
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
