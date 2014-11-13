package codeine.collectors;

import java.util.List;

import org.apache.log4j.Logger;

import codeine.configuration.IConfigurationManager;
import codeine.executer.Task;
import codeine.jsons.collectors.CollectorInfo;
import codeine.jsons.project.ProjectJson;

import com.google.common.collect.Lists;

public class CollectorsRunner implements Task {

	private static final Logger log = Logger.getLogger(CollectorsRunner.class);
	private IConfigurationManager configurationManager;
	private String projectName;
	
	@Override
	public void run() {
		List<CollectorInfo> collectors = Lists.newArrayList(project().collectors());
		for (CollectorInfo collector : collectors) {
			
		}
	}

	private ProjectJson project() {
		return configurationManager.getProjectForName(projectName);
	}
	
	
	
	
	
}
