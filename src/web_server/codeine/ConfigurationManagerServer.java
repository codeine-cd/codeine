package codeine;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.configuration.ConfigurationReadManagerServer;
import codeine.configuration.PathHelper;
import codeine.db.ProjectsConfigurationConnector;
import codeine.db.mysql.connectors.ProjectConfigurationDatabaseConnectorListProvider;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.utils.FilesUtils;
import codeine.utils.JsonFileUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ConfigurationManagerServer extends ConfigurationReadManagerServer
{
	private static final Logger log = Logger.getLogger(ConfigurationManagerServer.class);

	private ProjectConfigurationInPeerUpdater projectsUpdater;

	private PathHelper pathHelper;

	private JsonFileUtils jsonFileUtils;
	private List<ProjectsConfigurationConnector> statusDatabaseConnectorList = Lists.newArrayList();
	
	@Inject
	public ConfigurationManagerServer(JsonFileUtils jsonFileUtils, PathHelper pathHelper, ProjectConfigurationInPeerUpdater projectsUpdater, ProjectConfigurationDatabaseConnectorListProvider statusDatabaseConnectorListProvider)
	{
		super(jsonFileUtils, pathHelper);
		this.jsonFileUtils = jsonFileUtils;
		this.pathHelper = pathHelper;
		this.projectsUpdater = projectsUpdater; 
		statusDatabaseConnectorList = statusDatabaseConnectorListProvider.get();
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
		for (ProjectsConfigurationConnector projectsConfigurationConnector : statusDatabaseConnectorList) {
			projectsConfigurationConnector.deleteProject(projectToDelete);
		}
	}
	
	
	public synchronized void updateProject(ProjectJson updatedProject) {
		log.info("updating project " + updatedProject);
		String file = pathHelper.getProjectsDir() + "/" + updatedProject.name() + "/" + Constants.PROJECT_CONF_FILE;
		jsonFileUtils.setContent(file, updatedProject);
		Map<String, ProjectJson> newList = Maps.newHashMap();
		newList.putAll(projects());
		ProjectJson previousProject = newList.put(updatedProject.name(), updatedProject);
		projects(newList);
		updateProjectInDb(updatedProject);
		projectsUpdater.updatePeers(updatedProject, previousProject);
	}
	
	public synchronized void updateDb() {
		for (ProjectJson project : projects().values()) {
			updateProjectInDb(project);
		}
		projectsUpdater.updateAllPeers();
	}

	private void updateProjectInDb(ProjectJson project) {
		for (ProjectsConfigurationConnector projectsConfigurationConnector : statusDatabaseConnectorList) {
			projectsConfigurationConnector.updateProject(project);
		}
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
