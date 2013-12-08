package codeine.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.api.NodeGetter;
import codeine.api.NodeWithMonitorsInfo;
import codeine.configuration.IConfigurationManager;
import codeine.executer.Task;
import codeine.jsons.project.ProjectJson;
import codeine.utils.LimitedQueue;
import codeine.utils.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

public class MonitorsStatistics implements Task{

	@Inject	private IConfigurationManager configurationManager;
	@Inject	private NodeGetter nodesGetter;
	@Inject private Gson gson;
	
	private static final Logger log = Logger.getLogger(MonitorsStatistics.class);
	private static final int MAX_SIZE = 2 * 24 * 7 * 1;
	private Map<String, LimitedQueue<MonitorStatusItem>> data = Maps.newConcurrentMap();
	
	public static final long SLEEP_TIME = TimeUnit.MINUTES.toMillis(2);
	
	public MonitorsStatistics() {
		super();
	}

	public String getDataJson(String projectName) {
		LimitedQueue<MonitorStatusItem> d = data.get(projectName);
		if (null == d){
			return "";
		}
		ArrayList<MonitorStatusItem> l;
		synchronized (d) {
			l = Lists.newArrayList(d);
		}
		Collections.reverse(l);
		return gson.toJson(l);
	}

	@Override
	public void run() {
		collectData();
	}
	
	private void collectData() {
		long currentTime = System.currentTimeMillis();
		List<ProjectJson> projects = configurationManager.getConfiguredProjects();
		for (ProjectJson projectJson : projects) {
			int fail = 0, success = 0;
			List<NodeWithMonitorsInfo> nodes = nodesGetter.getNodes(projectJson.name());
			for (NodeWithMonitorsInfo nodeWithMonitorsInfo : nodes) {
				if (nodeWithMonitorsInfo.status()) {
					success++;
				} else {
					fail++;
				}
			}
			LimitedQueue<MonitorStatusItem> projectData = data.get(projectJson.name());
			if (projectData == null) {
				projectData = new LimitedQueue<>(MAX_SIZE);
				data.put(projectJson.name(), projectData);
			}
			MonitorStatusItem item = new MonitorStatusItem(StringUtils.formatDate(currentTime), success, fail);
			synchronized (projectData) {
				projectData.addFirst(item);
			}
			log.info("Project: " + projectJson.name() + " , Total Success: " + success + " , Total Fail: " + fail);
		}
	}
}
