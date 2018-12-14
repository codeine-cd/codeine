package codeine.db.mysql.connectors;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.google.common.collect.Maps;
import java.util.List;

import java.util.Map;
import javax.inject.Inject;

import codeine.db.IStatusDatabaseConnector;
import codeine.db.mysql.DbUtils;
import codeine.db.mysql.StaticMysqlHostSelector;
import codeine.jsons.global.ExperimentalConfJsonStore;
import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.jsons.global.MysqlConfigurationJson;

import com.google.common.collect.Lists;
import com.google.gson.Gson;

public class StatusDatabaseConnectorListProvider {
	
	@Inject private GlobalConfigurationJsonStore globalConfigurationJsonStore;
	@Inject private Gson gson;
	@Inject private ExperimentalConfJsonStore webConfJsonStore;
	@Inject private HealthCheckRegistry healthCheckRegistry;
	@Inject private MetricRegistry metricRegistry;

	private Map<MysqlConfigurationJson, DbUtils> dbUtilsMap = Maps.newHashMap();

	public List<IStatusDatabaseConnector> get() {
		List<IStatusDatabaseConnector> $ = Lists.newArrayList();
		for (MysqlConfigurationJson m : globalConfigurationJsonStore.get().mysql()) {
			DbUtils dbUtils = getDbUtils(m);
			IStatusDatabaseConnector c = new StatusMysqlConnector(dbUtils, gson, webConfJsonStore);
			$.add(c);
		}
		return $;
	}

	private DbUtils getDbUtils(MysqlConfigurationJson m) {
		return dbUtilsMap.computeIfAbsent(m,
                    mysqlConfigurationJson -> new DbUtils(new StaticMysqlHostSelector(m),
                        globalConfigurationJsonStore, healthCheckRegistry, metricRegistry));
	}


}

