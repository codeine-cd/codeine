package codeine.db.mysql.connectors;

import java.util.List;

import javax.inject.Inject;

import codeine.db.mysql.DbUtils;
import codeine.db.mysql.StaticMysqlHostSelector;
import codeine.jsons.global.ExperimentalConfJsonStore;
import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.jsons.global.MysqlConfigurationJson;

import com.google.common.collect.Lists;
import com.google.gson.Gson;

public class AlertsMysqlConnectorDatabaseConnectorListProvider {
	
	@Inject private GlobalConfigurationJsonStore globalConfigurationJsonStore;
	@Inject private Gson gson;
	@Inject private ExperimentalConfJsonStore webConfJsonStore;

	public List<AlertsMysqlConnector> get() {
		List<AlertsMysqlConnector> $ = Lists.newArrayList();
		for (MysqlConfigurationJson m : globalConfigurationJsonStore.get().mysql()) {
			DbUtils dbUtils = new DbUtils(new StaticMysqlHostSelector(m));
			AlertsMysqlConnector c = new AlertsMysqlConnector(dbUtils, gson, webConfJsonStore);
			$.add(c);
		}
		return $;
	}

	
	
	

}
