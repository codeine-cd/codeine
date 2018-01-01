package codeine.db.mysql.connectors;

import java.util.List;

import javax.inject.Inject;

import codeine.db.ProjectsConfigurationConnector;
import codeine.db.mysql.DbUtils;
import codeine.db.mysql.StaticMysqlHostSelector;
import codeine.jsons.global.ExperimentalConfJsonStore;
import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.jsons.global.MysqlConfigurationJson;

import com.google.common.collect.Lists;
import com.google.gson.Gson;

public class ProjectConfigurationDatabaseConnectorListProvider {
	
	@Inject private GlobalConfigurationJsonStore globalConfigurationJsonStore;
	@Inject private Gson gson;
	@Inject private ExperimentalConfJsonStore webConfJsonStore;

	public List<ProjectsConfigurationConnector> get() {
		List<ProjectsConfigurationConnector> $ = Lists.newArrayList();
		for (MysqlConfigurationJson m : globalConfigurationJsonStore.get().mysql()) {
			DbUtils dbUtils = new DbUtils(new StaticMysqlHostSelector(m),
				globalConfigurationJsonStore.get().max_db_pool_size(),
				globalConfigurationJsonStore.get().min_db_pool_size());
			ProjectsConfigurationConnector c = new ProjectsConfigurationMysqlConnector(dbUtils, gson, webConfJsonStore);
			$.add(c);
		}
		return $;
	}

	
	
	

}
