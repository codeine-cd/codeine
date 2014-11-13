package codeine.collectors;

import java.util.Collection;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.api.NodeInfo;
import codeine.configuration.IConfigurationManager;
import codeine.configuration.PathHelper;
import codeine.executer.Task;
import codeine.jsons.project.ProjectJson;
import codeine.utils.FilesUtils;

import com.google.inject.assistedinject.Assisted;

public class CollectorsRunner implements Task {

	private static final Logger log = Logger.getLogger(CollectorsRunner.class);
	@Inject private IConfigurationManager configurationManager;
	@Inject private PathHelper pathHelper;
	private CollectorsListHolder collectorsListHolder;
	private String projectName;
	private NodeInfo node;
	
	@Inject
	public CollectorsRunner(@Assisted String projectName, @Assisted NodeInfo node, CollectorsListHolderFactory collectorsListHolderFactory) {
		super();
		this.projectName = projectName;
		this.node = node;
		collectorsListHolder = collectorsListHolderFactory.create(projectName, node);
	}

	@Override
	public void run() {
		log.info("starting collection");
		Collection<OneCollectorRunner> collectors = collectorsListHolder.getCurrentListAndRemoveOldCollectors();
		for (OneCollectorRunner c : collectors) {
			c.run();
		}
	}

	public void init() {
		String monitorOutputDirWithNode = pathHelper.getMonitorOutputDirWithNode(project().name(), node.name());
		FilesUtils.mkdirs(monitorOutputDirWithNode);
	}
	
	private ProjectJson project() {
		return configurationManager.getProjectForName(projectName);
	}
	
	
	
	
	
}
