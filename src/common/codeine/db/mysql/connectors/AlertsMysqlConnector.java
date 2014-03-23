package codeine.db.mysql.connectors;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.db.IAlertsDatabaseConnector;
import codeine.db.mysql.DbUtils;
import codeine.jsons.global.ExperimentalConfJsonStore;
import codeine.jsons.mails.AlertsCollectionType;
import codeine.jsons.mails.CollectorNotificationJson;
import codeine.utils.ExceptionUtils;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;

public class AlertsMysqlConnector implements IAlertsDatabaseConnector{
	private static final Logger log = Logger.getLogger(AlertsMysqlConnector.class);
	private DbUtils dbUtils;
	private Gson gson;
	private ExperimentalConfJsonStore webConfJsonStore;
	private String tableName = "Alerts";
	
	
	
	@Inject
	public AlertsMysqlConnector(DbUtils dbUtils, Gson gson, ExperimentalConfJsonStore webConfJsonStore) {
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
		String colsDefinition = "id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, data text, collection_type_update_time BIGINT, collection_type BIGINT";
		dbUtils.executeUpdate("create table if not exists " + tableName + " (" + colsDefinition + ")");
	}

	@Override
	public void put(CollectorNotificationJson collectorNotificationJson) {
		String json = gson.toJson(collectorNotificationJson);
		dbUtils.executeUpdate("INSERT INTO "+tableName+" (data) VALUES (?)", json);
	}

	@Override
	public Multimap<String, CollectorNotificationJson> getAlertsAndUpdate(final AlertsCollectionType collType) {
		final AtomicInteger count = new AtomicInteger(0);
		final Multimap<String, CollectorNotificationJson> $ = HashMultimap.create();
		if (webConfJsonStore.get().readonly_web_server()) {
			log.info("read only mode");
			return $;
		}
		Function<ResultSet, Void> function = new Function<ResultSet, Void>() {
			@Override
			public Void apply(ResultSet rs){
				try {
					String data = rs.getString("data");
//					Long type = rs.getLong("collection_type");
//					int id = rs.getInt("id");
					CollectorNotificationJson n = gson.fromJson(data, CollectorNotificationJson.class);
					rs.updateLong("collection_type_update_time", System.currentTimeMillis());
					rs.updateLong("collection_type", collType.toLong());
					$.put(n.project_name(),n);
					rs.updateRow();
					count.incrementAndGet();
					return null;
				} catch (SQLException e) {
					throw ExceptionUtils.asUnchecked(e); 
				}
			}
		};
		dbUtils.executeUpdateableQuery("SELECT id, data, collection_type_update_time, collection_type FROM " + tableName + 
				" WHERE collection_type < " + collType.toLong() + " OR collection_type IS NULL" , function);
		if (count.intValue() > 0){
			log.info("handled col type " + collType + " with num of events " + count.intValue());
		}
		return $;
	}

	@Override
	public void removeOldAlerts() {
		if (webConfJsonStore.get().readonly_web_server()) {
			log.info("read only mode");
			return;
		}
		long timeToRemove = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7);
		log.info("will remove older than " + timeToRemove);
		String deleteSql = "delete from " + tableName + " where collection_type_update_time < " + timeToRemove + " AND collection_type = " + AlertsCollectionType.Daily.toLong();
		dbUtils.executeUpdate(deleteSql);
	}

}
