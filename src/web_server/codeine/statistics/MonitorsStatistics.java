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
import codeine.configuration.ConfigurationReadManagerServer;
import codeine.configuration.IConfigurationManager;
import codeine.configuration.PathHelper;
import codeine.jsons.project.ProjectJson;
import codeine.utils.FilesUtils;
import codeine.utils.LimitedQueue;
import codeine.utils.SerializationUtils;
import codeine.utils.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MonitorsStatistics implements IMonitorStatistics{

	private static final Logger log = Logger.getLogger(MonitorsStatistics.class);
	private static final int SAMPLE_TIME_MINUTES = 2;
	private static final int MAX_SIZE =(int) (TimeUnit.DAYS.toMinutes(30) / SAMPLE_TIME_MINUTES);
	@Inject	private IConfigurationManager configurationManager;
	@Inject	private NodeGetter nodesGetter;
	@Inject private PathHelper pathHelper;
	
	private Map<String, LimitedQueue<MonitorStatusItem>> data = Maps.newConcurrentMap();
	
	public static final long SLEEP_TIME = TimeUnit.MINUTES.toMillis(SAMPLE_TIME_MINUTES);
	
	public MonitorsStatistics() {
		super();
	}

	public void restore() {
		try {
			String statisticsFile = pathHelper.getStatisticsFile();
			if (FilesUtils.exists(statisticsFile)) {
				log.info("reading statistics from " + statisticsFile);
				data = SerializationUtils.fromFile(statisticsFile);
			} else {
				//make sure directory exists
				FilesUtils.mkdirs(pathHelper.getPersistentDir());
			}
		} catch (Exception e) {
			log.warn("failed to read statistics,  will reset", e);
		}
	}

	/* (non-Javadoc)
	 * @see codeine.statistics.IMonitorStatistics#getDataJson(java.lang.String)
	 */
	@Override
	public List<MonitorStatusItem> getData(String projectName) {
		LimitedQueue<MonitorStatusItem> d = data.get(projectName);
		if (null == d){
			return Lists.newArrayList();
		}
		ArrayList<MonitorStatusItem> l;
		synchronized (d) {
			l = Lists.newArrayList(d.subList(0, Math.min(d.size(), 1000)));
		}
		Collections.reverse(l);
		return l;
	}

	@Override
	public void run() {
		collectData();
	}
	
	private void collectData() {
		long currentTime = System.currentTimeMillis();
		List<ProjectJson> projects = configurationManager.getConfiguredProjects();
		projects.add(ConfigurationReadManagerServer.NODES_INTERNAL_PROJECT);
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
			if (success + fail == 0 && projectData.isEmpty()){
				log.info("ignoring empty statistics on project " + projectJson.name());
				continue;
			}
			MonitorStatusItem item = new MonitorStatusItem(StringUtils.formatDate(currentTime), success, fail);
			synchronized (projectData) {
				projectData.addFirst(item);
			}
			log.info("Project: " + projectJson.name() + " , Total Success: " + success + " , Total Fail: " + fail);
		}
		log.info("saving statistics data to file");
		SerializationUtils.toFile(pathHelper.getStatisticsFile(), data);
	}
}
