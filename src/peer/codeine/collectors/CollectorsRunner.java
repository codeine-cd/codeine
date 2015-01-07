package codeine.collectors;

import java.util.Collection;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.api.NodeInfo;
import codeine.collectors.tags.TagsCollectorRunner;
import codeine.collectors.tags.TagsCollectorRunnerFactory;
import codeine.collectors.version.VersionCollectorRunner;
import codeine.collectors.version.VersionCollectorRunnerFactory;
import codeine.configuration.IConfigurationManager;
import codeine.configuration.PathHelper;
import codeine.executer.Task;
import codeine.jsons.project.ProjectJson;
import codeine.utils.FilesUtils;
import codeine.utils.StringUtils;

import com.google.common.collect.Lists;
import com.google.inject.assistedinject.Assisted;

public class CollectorsRunner implements Task {

	private static final Logger log = Logger.getLogger(CollectorsRunner.class);
	@Inject private IConfigurationManager configurationManager;
	@Inject private PathHelper pathHelper;
	@Inject private VersionCollectorRunnerFactory versionCollectorRunnerFactory;
	private CollectorsListHolder collectorsListHolder;
	private String projectName;
	private NodeInfo node;
	private VersionCollectorRunner versionCollector;
	private TagsCollectorRunner tagsCollector;

	@Inject
	public CollectorsRunner(@Assisted String projectName, @Assisted NodeInfo node, 
			CollectorsListHolderFactory collectorsListHolderFactory, TagsCollectorRunnerFactory tagsCollectorRunnerFactory) {
		super();
		this.projectName = projectName;
		this.node = node;
		collectorsListHolder = collectorsListHolderFactory.create(projectName, node);
		tagsCollector = tagsCollectorRunnerFactory.create(projectName, node);
	}

	@Override
	public void run() {
		log.info("starting collection");
		ProjectJson project = project();
		Collection<IOneCollectorRunner> collectors = Lists.newArrayList();
		if (!StringUtils.isEmpty(project.version_detection_script())) {
			if (null == versionCollector) {
				versionCollector = versionCollectorRunnerFactory.create(project.name(), node);
			}
			collectors.add(versionCollector);
		}
		collectors.add(tagsCollector);
		collectors.addAll(collectorsListHolder.getCurrentListAndRemoveOldCollectors());
		for (IOneCollectorRunner c : collectors) {
			c.execute();
		}
	}

	public void init() {
		String monitorOutputDirWithNode = pathHelper.getCollectorOutputDirWithNode(project().name(), node.name());
		FilesUtils.mkdirs(monitorOutputDirWithNode);
	}

	private ProjectJson project() {
		return configurationManager.getProjectForName(projectName);
	}





}
