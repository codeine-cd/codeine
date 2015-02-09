package codeine.db.mysql.connectors;

import java.util.List;

import javax.inject.Inject;

import codeine.configuration.FeatureFlags;
import codeine.db.mysql.DbUtils;
import codeine.db.mysql.StaticMysqlHostSelector;
import codeine.jsons.global.ExperimentalConfJsonStore;
import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.jsons.global.MysqlConfigurationJson;

import com.google.common.collect.Lists;
import com.google.gson.Gson;

public class NotificationsMysqlConnectorDatabaseConnectorListProvider {
	
	@Inject private GlobalConfigurationJsonStore globalConfigurationJsonStore;
	@Inject private Gson gson;
	@Inject private ExperimentalConfJsonStore webConfJsonStore;
	@Inject private FeatureFlags featureFlags;

	public List<NotificationsMysqlConnector> get() {
		List<NotificationsMysqlConnector> $ = Lists.newArrayList();
		for (MysqlConfigurationJson m : globalConfigurationJsonStore.get().mysql()) {
			DbUtils dbUtils = new DbUtils(new StaticMysqlHostSelector(m));
			NotificationsMysqlConnector c = new NotificationsMysqlConnector(dbUtils, gson, webConfJsonStore, featureFlags);
			$.add(c);
		}
		return $;
	}

	
	
	

}
