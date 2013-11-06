package codeine.configuration;

import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.jsons.nodes.NodesManager;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.utils.FilesUtils;
import codeine.utils.JsonFileUtils;

import com.google.common.collect.Lists;

public class ConfigurationManager
{
	private static final Logger log = Logger.getLogger(ConfigurationManager.class);
	
	private List<ProjectJson> projects = Lists.newArrayList();
	private JsonFileUtils jsonFileUtils;
	private NodesManager nodesManager;
	private PathHelper pathHelper;
	
	@Inject
	public ConfigurationManager(JsonFileUtils jsonFileUtils, NodesManager nodesManager, PathHelper pathHelper)
	{
		this.jsonFileUtils = jsonFileUtils; 
		this.nodesManager = nodesManager; 
		this.pathHelper = pathHelper; 
		init();
	}

	private void init() {
		try {
			log.info("loading configuration.");
			List<String> files = FilesUtils.getFilesInDir(pathHelper.getProjectsDir());
			for (String file : files) {
				try {
					if (file.startsWith(".")){
						log.info("will ignore project dir " + file);
						continue;
					}
					String file2 = pathHelper.getProjectsDir() + "/" + file + "/" + Constants.PROJECT_CONF_FILE;
					ProjectJson projectJson = jsonFileUtils.getConfFromFile(file2, ProjectJson.class);
					nodesManager.init(projectJson);
					projects.add(projectJson);
					FilesUtils.mkdirs(pathHelper.getPluginsOutputDir(projectJson.name()));
				} catch (Exception e) {
					log.error("failed to configure project " + file, e);
				}
			}
		} catch (RuntimeException e) {
			log.error("error", e);
			throw e;
		}
	}
	
	public List<ProjectJson> getConfiguredProjects() {
		return projects;
	}
	
	public ProjectJson getProjectForName(String projectName) {
		List<ProjectJson> configuredProjects = getConfiguredProjects();
		for (ProjectJson projectJson : configuredProjects) {
			if (projectName.equals(projectJson.name())){
				return projectJson;
			}
		}
		throw new IllegalArgumentException("project not found " + projectName);
	}
}
