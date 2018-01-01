package codeine.db.mysql.connectors;

import com.google.common.collect.Maps;
import java.util.List;

import java.util.Map;
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

	private Map<MysqlConfigurationJson, DbUtils> dbUtilsMap = Maps.newHashMap();

	public List<ProjectsConfigurationConnector> get() {
		List<ProjectsConfigurationConnector> $ = Lists.newArrayList();
		for (MysqlConfigurationJson m : globalConfigurationJsonStore.get().mysql()) {
			DbUtils dbUtils = getDbUtils(m);
			ProjectsConfigurationConnector c = new ProjectsConfigurationMysqlConnector(dbUtils, gson, webConfJsonStore);
			$.add(c);
		}
		return $;
	}

	private DbUtils getDbUtils(MysqlConfigurationJson m) {
		return dbUtilsMap.computeIfAbsent(m,
                    mysqlConfigurationJson -> new DbUtils(new StaticMysqlHostSelector(m),
                        globalConfigurationJsonStore));
	}


}
