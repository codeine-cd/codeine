package codeine;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.configuration.ConfigurationReadManagerServer;
import codeine.configuration.NodeMonitor;
import codeine.configuration.PathHelper;
import codeine.db.ProjectsConfigurationConnector;
import codeine.db.mysql.connectors.ProjectConfigurationDatabaseConnectorListProvider;
import codeine.executer.ThreadPoolUtils;
import codeine.jsons.collectors.CollectorInfo;
import codeine.jsons.collectors.CollectorInfo.CollectorType;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.utils.ExceptionUtils;
import codeine.utils.FilesUtils;
import codeine.utils.JsonFileUtils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class ConfigurationManagerServer extends ConfigurationReadManagerServer
{
	private static final Logger log = Logger.getLogger(ConfigurationManagerServer.class);
	private static final int MAX_NUM_OF_DB_ENTRIES = 30;
	private static final int NUM_OF_THREADS_FOR_EACH_DB = 1;

	private ProjectConfigurationInPeerUpdater projectsUpdater;
	private PathHelper pathHelper;
	private JsonFileUtils jsonFileUtils;
	private ProjectConfigurationDatabaseConnectorListProvider statusDatabaseConnectorListProvider;
	private Cache<String, ThreadPoolExecutor> dbUpdateThreadsMap = CacheBuilder.newBuilder().maximumSize(MAX_NUM_OF_DB_ENTRIES).build();

	@Inject
	public ConfigurationManagerServer(JsonFileUtils jsonFileUtils, PathHelper pathHelper, ProjectConfigurationInPeerUpdater projectsUpdater, ProjectConfigurationDatabaseConnectorListProvider statusDatabaseConnectorListProvider)
	{
		super(jsonFileUtils, pathHelper);
		this.jsonFileUtils = jsonFileUtils;
		this.pathHelper = pathHelper;
		this.projectsUpdater = projectsUpdater;
		this.statusDatabaseConnectorListProvider = statusDatabaseConnectorListProvider; 
	}

	public void deleteProject(final ProjectJson projectToDelete) {
		String dirName = pathHelper.getProjectsDir() + "/" + projectToDelete.name();
		FilesUtils.delete(dirName);
		projects().remove(projectToDelete.name());
		for (final ProjectsConfigurationConnector projectsConfigurationConnector : statusDatabaseConnectorListProvider.get()) {
			getUpdateThreadPool(projectsConfigurationConnector.getKey()).execute(new Runnable() {
				@Override
				public void run() {
					try {
						projectsConfigurationConnector.deleteProject(projectToDelete);
					} catch (Exception e) {
						log.warn("cannot update project in database " + projectToDelete.name() + " " + projectsConfigurationConnector, e);
					}
				}
			});
		}
	}


	private ThreadPoolExecutor getUpdateThreadPool(String key) {
		try {
			return dbUpdateThreadsMap.get(key, new Callable<ThreadPoolExecutor>() {
				@Override
				public ThreadPoolExecutor call() throws Exception {
					return ThreadPoolUtils.newThreadPool(NUM_OF_THREADS_FOR_EACH_DB, "ConfigurationManagerServer-DB-Update");
				}
			});
		} catch (ExecutionException e) {
			throw ExceptionUtils.asUnchecked(e);
		}
	}

	public boolean updateProject(final ProjectJson updatedProject) {
		log.info("updating project " + updatedProject);
		changeMonitorsToCollectors(updatedProject);
		String file = pathHelper.getProjectsDir() + "/" + updatedProject.name() + "/" + Constants.PROJECT_CONF_FILE;
		jsonFileUtils.setContent(file, updatedProject);
		final ProjectJson previousProject = projects().put(updatedProject.name(), updatedProject);
		log.info("updating project in db and peers " + updatedProject.name());
		updateProjectInDb(updatedProject);
		projectsUpdater.updatePeers(updatedProject, previousProject);
		return null != previousProject;
	}

	private void changeMonitorsToCollectors(ProjectJson updatedProject) {
		for (NodeMonitor monitorInfo : updatedProject.monitors()) {
			String name = monitorInfo.name();
			CollectorType type = CollectorType.Monitor;
			CollectorInfo collectorInfo = new CollectorInfo(name, monitorInfo.script_content(), monitorInfo.minInterval(), monitorInfo.credentials(), type, monitorInfo.notification_enabled());
			updatedProject.collectors().add(collectorInfo);
		}
		updatedProject.monitors().clear();
	}

	public void updateDb() {
		for (ProjectJson project : projects().values()) {
			updateProjectInDb(project);
		}
		projectsUpdater.updateAllPeers();
	}
	public void updateDb(String serverKey) {
		ProjectsConfigurationConnector projectsConfigurationConnector = getConnector(serverKey);
		for (ProjectJson project : projects().values()) {
			updateProjectInDb(project, projectsConfigurationConnector);
		}
	}

	private ProjectsConfigurationConnector getConnector(String serverKey) {
		for (final ProjectsConfigurationConnector projectsConfigurationConnector : statusDatabaseConnectorListProvider.get()) {
			if (projectsConfigurationConnector.getKey().equals(serverKey)) {
				return projectsConfigurationConnector;
			}
		}
		throw new IllegalArgumentException("server not found " + serverKey);
	}

	private void updateProjectInDb(final ProjectJson project) {
		for (final ProjectsConfigurationConnector projectsConfigurationConnector : statusDatabaseConnectorListProvider.get()) {
			updateProjectInDb(project, projectsConfigurationConnector);
		}
	}

	private void updateProjectInDb(final ProjectJson project,
			final ProjectsConfigurationConnector projectsConfigurationConnector) {
		getUpdateThreadPool(projectsConfigurationConnector.getKey()).execute(new Runnable() {
			@Override
			public void run() {
				try {
					projectsConfigurationConnector.updateProject(project);
				} catch (Exception e) {
					log.warn("cannot update project in database " + project.name() + " " + projectsConfigurationConnector, e);
				}
			}
		});
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

	public void reloadProject(String projectName) {
		ProjectJson project = getProjectFromDisk(projectName);
		final ProjectJson previousProject = projects().put(projectName, project);
		log.info("updating project in db and peers " + projectName);
		updateProjectInDb(project);
		projectsUpdater.updatePeers(project, previousProject);
	}

}
