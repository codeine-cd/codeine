package codeine;

import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.configuration.ConfigurationReadManagerServer;
import codeine.configuration.PathHelper;
import codeine.db.ProjectsConfigurationConnector;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.utils.FilesUtils;
import codeine.utils.JsonFileUtils;

import com.google.common.collect.Maps;

public class ConfigurationManagerServer extends ConfigurationReadManagerServer
{
	private static final Logger log = Logger.getLogger(ConfigurationManagerServer.class);

	private ProjectsConfigurationConnector projectsConfigurationConnector;
	private ProjectConfigurationInPeerUpdater projectsUpdater;

	private PathHelper pathHelper;

	private JsonFileUtils jsonFileUtils;
	
	@Inject
	public ConfigurationManagerServer(JsonFileUtils jsonFileUtils, PathHelper pathHelper, ProjectsConfigurationConnector projectsConfigurationConnector, ProjectConfigurationInPeerUpdater projectsUpdater)
	{
		super(jsonFileUtils, pathHelper);
		this.jsonFileUtils = jsonFileUtils;
		this.projectsConfigurationConnector = projectsConfigurationConnector;
		this.pathHelper = pathHelper;
		this.projectsUpdater = projectsUpdater; 
	}

	public synchronized void deleteProject(ProjectJson projectToDelete) {
		String dirName = pathHelper.getProjectsDir() + "/" + projectToDelete.name();
		FilesUtils.delete(dirName);
		Map<String, ProjectJson> newList = Maps.newHashMap();
		for (Entry<String, ProjectJson> entry : projects().entrySet()) {
			if (!entry.getValue().name().equals(projectToDelete.name()))
				newList.put(entry.getKey(), entry.getValue());
		}
		projects(newList);
		projectsConfigurationConnector.deleteProject(projectToDelete);
	}
	
	
	public synchronized void updateProject(ProjectJson updatedProject) {
		log.info("updating project " + updatedProject);
		String file = pathHelper.getProjectsDir() + "/" + updatedProject.name() + "/" + Constants.PROJECT_CONF_FILE;
		jsonFileUtils.setContent(file, updatedProject);
		Map<String, ProjectJson> newList = Maps.newHashMap();
		newList.putAll(projects());
		newList.put(updatedProject.name(), updatedProject);
		projects(newList);
		updateDb();
	}
	
	public synchronized void updateDb() {
		for (ProjectJson project : projects().values()) {
			//TODO update in all mysql instances
			projectsConfigurationConnector.updateProject(project);
		}
		projectsUpdater.updateAllPeers();
	}

	public void createNewProject(ProjectJson project) {
		String dir = pathHelper.getProjectsDir() + "/" + project.name();
		if (FilesUtils.exists(dir)) {
			throw new RuntimeException("project '"+ project.name() + "' already exists");
		}
		log.info("creating project in " + dir);
		FilesUtils.mkdirs(dir);
		updateProject(project);
	}

}
