package codeine.collectors;

import java.io.File;
import java.util.Map;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.api.NodeInfo;
import codeine.configuration.PathHelper;
import codeine.jsons.collectors.CollectorInfo;
import codeine.jsons.project.ProjectJson;
import codeine.utils.os_process.ShellScript;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.google.inject.assistedinject.Assisted;

public class OneCollectorRunner {

	private static final Logger log = Logger.getLogger(OneCollectorRunner.class);
	@Inject private PathHelper pathHelper;
	private CollectorInfo collectorInfo;
	private ProjectJson project;
	private NodeInfo node;
	private Long lastRuntime;
	private Stopwatch stopwatch;
	
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
		stopwatch = Stopwatch.createStarted();
		String key = pathHelper.getMonitorsDir(project.name()) + File.separator + node.name() + "_" + collectorInfo.name();
		Map<String, String> env = Maps.newHashMap();
		ShellScript shellScript = new ShellScript(key, collectorInfo.script_content(), project.operating_system(), null, pathHelper.getProjectDir(project.name()), env);
//		String fileName = shellScript.create();
//		shellScript.execute();
		//TODO continue here like in RunMonitors
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
