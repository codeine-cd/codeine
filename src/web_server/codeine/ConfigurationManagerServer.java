package codeine;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.configuration.ConfigurationReadManagerServer;
import codeine.configuration.PathHelper;
import codeine.db.ProjectsConfigurationConnector;
import codeine.db.mysql.connectors.ProjectConfigurationDatabaseConnectorListProvider;
import codeine.executer.ThreadPoolUtils;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.utils.FilesUtils;
import codeine.utils.JsonFileUtils;

import com.google.common.collect.Lists;

public class ConfigurationManagerServer extends ConfigurationReadManagerServer
{
	private static final Logger log = Logger.getLogger(ConfigurationManagerServer.class);
	private static final int NUM_OF_THREADS = 3;

	private ProjectConfigurationInPeerUpdater projectsUpdater;
	private PathHelper pathHelper;
	private JsonFileUtils jsonFileUtils;
	private List<ProjectsConfigurationConnector> statusDatabaseConnectorList = Lists.newArrayList();
	private ThreadPoolExecutor updateThreadPool = ThreadPoolUtils.newThreadPool(NUM_OF_THREADS);
	
	@Inject
	public ConfigurationManagerServer(JsonFileUtils jsonFileUtils, PathHelper pathHelper, ProjectConfigurationInPeerUpdater projectsUpdater, ProjectConfigurationDatabaseConnectorListProvider statusDatabaseConnectorListProvider)
	{
		super(jsonFileUtils, pathHelper);
		this.jsonFileUtils = jsonFileUtils;
		this.pathHelper = pathHelper;
		this.projectsUpdater = projectsUpdater; 
		statusDatabaseConnectorList = statusDatabaseConnectorListProvider.get();
	}

	public void deleteProject(final ProjectJson projectToDelete) {
		String dirName = pathHelper.getProjectsDir() + "/" + projectToDelete.name();
		FilesUtils.delete(dirName);
		projects().remove(projectToDelete.name());
		updateThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				for (ProjectsConfigurationConnector projectsConfigurationConnector : statusDatabaseConnectorList) {
					projectsConfigurationConnector.deleteProject(projectToDelete);
				}
			}
		});
	}
	
	
	public void updateProject(final ProjectJson updatedProject) {
		log.info("updating project " + updatedProject);
		String file = pathHelper.getProjectsDir() + "/" + updatedProject.name() + "/" + Constants.PROJECT_CONF_FILE;
		jsonFileUtils.setContent(file, updatedProject);
		final ProjectJson previousProject = projects().put(updatedProject.name(), updatedProject);
		updateThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				log.info("updating project in db and peers " + updatedProject.name());
				updateProjectInDb(updatedProject);
				projectsUpdater.updatePeers(updatedProject, previousProject);
			}
		});
	}
	
	public void updateDb() {
		for (ProjectJson project : projects().values()) {
			updateProjectInDb(project);
		}
		projectsUpdater.updateAllPeers();
	}

	private void updateProjectInDb(ProjectJson project) {
		for (ProjectsConfigurationConnector projectsConfigurationConnector : statusDatabaseConnectorList) {
			try {
				projectsConfigurationConnector.updateProject(project);
			} catch (Exception e) {
				log.warn("cannot update project in database " + project.name(), e);
			}
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
