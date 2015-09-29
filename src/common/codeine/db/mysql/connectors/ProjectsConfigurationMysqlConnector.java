package codeine.db.mysql.connectors;

import codeine.db.ProjectsConfigurationConnector;
import codeine.db.mysql.DbUtils;
import codeine.jsons.global.ExperimentalConfJsonStore;
import codeine.jsons.project.ProjectJson;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.util.Map;

public class ProjectsConfigurationMysqlConnector implements ProjectsConfigurationConnector{
	
	private static final Logger log = Logger.getLogger(ProjectsConfigurationMysqlConnector.class);
	@Inject
	private DbUtils dbUtils;
	@Inject
	private Gson gson;
	@Inject	private ExperimentalConfJsonStore webConfJsonStore;

	private static final String TABLE_NAME = "ProjectsConfiguration";

	
	public ProjectsConfigurationMysqlConnector() {
		super();
	}

	
	public ProjectsConfigurationMysqlConnector(DbUtils dbUtils, Gson gson, ExperimentalConfJsonStore webConfJsonStore) {
		super();
		this.dbUtils = dbUtils;
		this.gson = gson;
		this.webConfJsonStore = webConfJsonStore;
	}


	public void createTables() {
		if (webConfJsonStore.get().readonly_web_server()) {
			log.info("read only mode");
			return;
		}
		String colsDefinition = "project_name CHAR(100) NOT NULL PRIMARY KEY, data MEDIUMTEXT";
		dbUtils.executeUpdate("create table if not exists " + TABLE_NAME + " (" + colsDefinition + ")");
	}

	@Override
	public Map<String, ProjectJson> getAllProjects() {
		final Map<String, ProjectJson> $ = Maps.newHashMap();
		Function<ResultSet, Void> function = new Function<ResultSet, Void>() {
			@Override
			public Void apply(ResultSet input) {
				String json = null;
				String project = null;
				try {
					project = input.getString("project_name");
					json = input.getString("data");
					$.put(project, gson.fromJson(json, ProjectJson.class));
				} catch (Exception e) {
					log.error("json is " + json);
					log.error("failed to read project from database " + project, e);
				}
				return null;
			}
		};
		dbUtils.executeQuery("select * from " + TABLE_NAME, function);
		return $;
	}
	
	@Override
	public void updateProject(ProjectJson project){
		log.info("updating project in database " + project.name() +  " " + dbUtils);
		if (webConfJsonStore.get().readonly_web_server()) {
			log.info("read only mode");
			return;
		}
		int executeUpdate = dbUtils.executeUpdate("REPLACE INTO "+TABLE_NAME+" (project_name, data) VALUES (?, ?)", project.name(), gson.toJson(project));
		if (executeUpdate == 0) {
			throw new RuntimeException("failed to update project " + project.name());
		}
	}

	@Override
	public void deleteProject(ProjectJson project) {
		log.info("deleting project from database " + project.name());
		if (webConfJsonStore.get().readonly_web_server()) {
			log.info("read only mode");
			return;
		}
		int deleted = dbUtils.executeUpdate("DELETE FROM "+TABLE_NAME+" WHERE project_name = '" + project.name() + "'");
		if (deleted == 0) {
			throw new RuntimeException("failed to delete project " + project.name());
		}
	}


	@Override
	public String getKey() {
		return dbUtils.server();
	}

}
