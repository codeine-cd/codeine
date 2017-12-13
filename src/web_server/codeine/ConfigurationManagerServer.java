package codeine;

import codeine.configuration.ConfigurationReadManagerServer;
import codeine.configuration.PathHelper;
import codeine.db.ProjectsConfigurationConnector;
import codeine.db.mysql.connectors.ProjectConfigurationDatabaseConnectorListProvider;
import codeine.executer.ThreadPoolUtils;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.utils.ExceptionUtils;
import codeine.utils.FilesUtils;
import codeine.utils.JsonFileUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import javax.inject.Inject;
import org.apache.log4j.Logger;

public class ConfigurationManagerServer extends ConfigurationReadManagerServer {

    private static final Logger log = Logger.getLogger(ConfigurationManagerServer.class);
    private static final int MAX_NUM_OF_DB_ENTRIES = 30;
    private static final int NUM_OF_THREADS_FOR_EACH_DB = 1;
    private Map<String, Object> projectLocks = Maps.newHashMap();

    private ProjectConfigurationInPeerUpdater projectsUpdater;
    private PathHelper pathHelper;
    private JsonFileUtils jsonFileUtils;
    private ProjectConfigurationDatabaseConnectorListProvider statusDatabaseConnectorListProvider;
    private Cache<String, ThreadPoolExecutor> dbUpdateThreadsMap = CacheBuilder.newBuilder()
        .maximumSize(MAX_NUM_OF_DB_ENTRIES).build();

    @Inject
    public ConfigurationManagerServer(JsonFileUtils jsonFileUtils, PathHelper pathHelper,
        ProjectConfigurationInPeerUpdater projectsUpdater,
        ProjectConfigurationDatabaseConnectorListProvider statusDatabaseConnectorListProvider) {
        super(jsonFileUtils, pathHelper);
        this.jsonFileUtils = jsonFileUtils;
        this.pathHelper = pathHelper;
        this.projectsUpdater = projectsUpdater;
        this.statusDatabaseConnectorListProvider = statusDatabaseConnectorListProvider;
    }

    public void deleteProject(final ProjectJson projectToDelete) {
        String dirName = pathHelper.getProjectsDir() + "/" + projectToDelete.name();
        synchronized (getProjectLock(projectToDelete)) {
            projectLocks.remove(projectToDelete.name());
            FilesUtils.delete(dirName);
            projects().remove(projectToDelete.name());
            for (final ProjectsConfigurationConnector projectsConfigurationConnector : statusDatabaseConnectorListProvider
                .get()) {
                getUpdateThreadPool(projectsConfigurationConnector.getKey())
                    .execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                projectsConfigurationConnector.deleteProject(projectToDelete);
                            } catch (Exception e) {
                                log.warn(
                                    "cannot update project in database " + projectToDelete.name()
                                        + " "
                                        + projectsConfigurationConnector, e);
                            }
                        }
                    });
            }
        }
    }


    private ThreadPoolExecutor getUpdateThreadPool(String key) {
        try {
            return dbUpdateThreadsMap.get(key, () -> ThreadPoolUtils.newThreadPool(NUM_OF_THREADS_FOR_EACH_DB,
                "ConfigurationManagerServer-DB-Update"));
        } catch (ExecutionException e) {
            throw ExceptionUtils.asUnchecked(e);
        }
    }

    private ProjectJson doUpdateProject(final ProjectJson updatedProject) {
        log.info("updating project " + updatedProject);
        String file = pathHelper.getProjectsDir() + "/" + updatedProject.name() + "/"
            + Constants.PROJECT_CONF_FILE;
        log.info("Setting new UUID to conf of project " + updatedProject.name());
        updatedProject.conf_uuid(UUID.randomUUID());
        jsonFileUtils.setContent(file, updatedProject);
        final ProjectJson previousProject = projects().put(updatedProject.name(), updatedProject);
        log.info("updating project in db and peers " + updatedProject.name());
        updateProjectInDb(updatedProject);
        projectsUpdater.updatePeers(updatedProject, previousProject);
        return previousProject;
    }

    public boolean updateProject(final ProjectJson updatedProject) {
        log.info("will validate configuration of project " + updatedProject);
        synchronized (getProjectLock(updatedProject)) {
            ProjectJson current = getProjectForName(updatedProject.name());
            validateConf(current, updatedProject);
            return doUpdateProject(updatedProject) != null;
        }
    }

    private Object getProjectLock(ProjectJson updatedProject) {
        return projectLocks.getOrDefault(updatedProject.name(), new Object());
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

    private void validateConf(ProjectJson current, ProjectJson updated) {
        if (current.conf_uuid() == null) {
            log.warn("Project " + current.name() + " has no UUID for the configuration");
            return;
        }
        if (!current.conf_uuid().equals(updated.conf_uuid())) {
            log.warn("Configuration of project " + current.name() + " was changed since the read "
                + "of the updated configuration. Current: " + current.conf_uuid() + " Updated:"
                + updated.conf_uuid());
            throw new RuntimeException(
                "Your configuration is not updated, please reload and try again");
        }
    }


    private ProjectsConfigurationConnector getConnector(String serverKey) {
        for (final ProjectsConfigurationConnector projectsConfigurationConnector : statusDatabaseConnectorListProvider
            .get()) {
            if (projectsConfigurationConnector.getKey().equals(serverKey)) {
                return projectsConfigurationConnector;
            }
        }
        throw new IllegalArgumentException("server not found " + serverKey);
    }

    private void updateProjectInDb(final ProjectJson project) {
        for (final ProjectsConfigurationConnector projectsConfigurationConnector : statusDatabaseConnectorListProvider
            .get()) {
            updateProjectInDb(project, projectsConfigurationConnector);
        }
    }

    private void updateProjectInDb(final ProjectJson project,
        final ProjectsConfigurationConnector projectsConfigurationConnector) {
        getUpdateThreadPool(projectsConfigurationConnector.getKey()).execute(() -> {
            try {
                projectsConfigurationConnector.updateProject(project);
            } catch (Exception e) {
                log.warn("cannot update project in database " + project.name() + " "
                    + projectsConfigurationConnector, e);
            }
        });
    }

    public void createNewProject(ProjectJson project) {
        String dir = pathHelper.getProjectsDir() + "/" + project.name();
        if (FilesUtils.exists(dir)) {
            throw new RuntimeException("project '" + project.name() + "' already exists");
        }
        log.info("creating project in " + dir);
        FilesUtils.mkdirs(dir);
        doUpdateProject(project);
    }

    public ProjectJson reloadProject(String projectName) {
        ProjectJson project = getProjectFromDisk(projectName);
        final ProjectJson previousProject = projects().put(projectName, project);
        log.info("updating project in db and peers " + projectName);
        updateProjectInDb(project);
        projectsUpdater.updatePeers(project, previousProject);
        return project;
    }

}
