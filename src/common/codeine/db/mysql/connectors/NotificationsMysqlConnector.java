package codeine.db.mysql.connectors;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.configuration.FeatureFlags;
import codeine.db.IAlertsDatabaseConnector;
import codeine.db.mysql.DbUtils;
import codeine.jsons.global.ExperimentalConfJsonStore;
import codeine.jsons.mails.AlertsCollectionType;
import codeine.jsons.mails.CollectorNotificationJson;
import codeine.utils.ExceptionUtils;

import com.google.common.base.Function;
import com.google.common.base.Stopwatch;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;

public class NotificationsMysqlConnector implements IAlertsDatabaseConnector{
	private static final Logger log = Logger.getLogger(NotificationsMysqlConnector.class);
	private DbUtils dbUtils;
	private Gson gson;
	private ExperimentalConfJsonStore webConfJsonStore;
	private FeatureFlags featureFlags;
	private static final String TABLE_NAME = "CollectorsNotifications";
	
	
	@Inject
	public NotificationsMysqlConnector(DbUtils dbUtils, Gson gson, ExperimentalConfJsonStore webConfJsonStore,
			FeatureFlags featureFlags) {
		super();
		this.dbUtils = dbUtils;
		this.gson = gson;
		this.webConfJsonStore = webConfJsonStore;
		this.featureFlags = featureFlags;
	}

	public void createTables() {
		if (webConfJsonStore.get().readonly_web_server()) {
			log.info("read only mode");
			return;
		}
		String colsDefinition = "id INT NOT NULL AUTO_INCREMENT, INDEX (id), notification_id VARCHAR(150) NOT NULL PRIMARY KEY, data text, collection_type_update_time BIGINT, collection_type BIGINT";
		dbUtils.executeUpdate("create table if not exists " + TABLE_NAME + " (" + colsDefinition + ")");
	}

	@Override
	public void put(CollectorNotificationJson collectorNotificationJson) {
		if (featureFlags.isNotificationsDisabled()) {
			log.info("notification disabled");
			return;
		}
		String json = gson.toJson(collectorNotificationJson);
		dbUtils.executeUpdate("REPLACE INTO "+TABLE_NAME+" (notification_id, data) VALUES (?,?)", collectorNotificationJson.notification_id(), json);
	}

	@Override
	public Multimap<String, CollectorNotificationJson> getAlertsAndUpdate(final AlertsCollectionType collType) {
		long time = System.currentTimeMillis();
		Stopwatch s = Stopwatch.createStarted();
		MapWithId $ = query(collType);
		String queryMessage = "query took " + s ;
		s = Stopwatch.createStarted();
		updateCollectionType(collType, time, $.maxId);
		log.info("getAlertsAndUpdate - " + dbUtils + " with num of events " + $.map.size() + ", " + queryMessage + ", update took " + s + " , handled col type " + collType);
		return $.map;
	}


	private void updateCollectionType(final AlertsCollectionType collType, long time, int maxId) {
		if (webConfJsonStore.get().readonly_web_server()) {
			log.info("read only mode");
		}
		String updateString = "UPDATE " + TABLE_NAME + " SET collection_type_update_time=" + time +
				",collection_type=" + collType.toLong() + 
				" WHERE id<=" + maxId + " AND ("
				+ "(collection_type < " + collType.toLong() + " AND collection_type >= " + collType.previousType().toLong() + ")";
		if (collType == AlertsCollectionType.Immediately) {
			updateString += " OR collection_type IS NULL";
		} 
		updateString += ")";
		dbUtils.executeUpdate(updateString);
	}
	private static class MapWithId {
		Multimap<String, CollectorNotificationJson> map;
		int maxId;
		public MapWithId(Multimap<String, CollectorNotificationJson> map, int maxId) {
			this.map = map;
			this.maxId = maxId;
		}
	}
	private MapWithId query(final AlertsCollectionType collType) {
		final AtomicInteger count = new AtomicInteger(0);
		final AtomicInteger maxId = new AtomicInteger(0);
		final Multimap<String, CollectorNotificationJson> $ = HashMultimap.create();
		Function<ResultSet, Void> function = new Function<ResultSet, Void>() {
			@Override
			public Void apply(ResultSet rs){
//				if (webConfJsonStore.get().readonly_web_server()) {
//					return null;
//				}
				try {
					String data = rs.getString("data");
//					Long type = rs.getLong("collection_type");
					int id = rs.getInt("id");
					maxId.set(Math.max(maxId.get(), id));
					CollectorNotificationJson n = gson.fromJson(data, CollectorNotificationJson.class);
					$.put(n.project_name(),n);
					count.incrementAndGet();
					return null;
				} catch (SQLException e) {
					throw ExceptionUtils.asUnchecked(e); 
				}
			}
		};
		dbUtils.executeQueryCompressed("SELECT id, data, collection_type_update_time, collection_type FROM " + TABLE_NAME + 
				" WHERE collection_type < " + collType.toLong() + " OR collection_type IS NULL" , function);
		log.debug("handled col type " + collType + " with num of events " + count.intValue() + " on " + dbUtils);
		return new MapWithId($, maxId.get());
	}

	@Override
	public void removeOldAlerts() {
		if (webConfJsonStore.get().readonly_web_server()) {
			log.info("read only mode");
			return;
		}
		long timeToRemove = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(3);
		log.info("will remove older than " + timeToRemove + " on " + dbUtils);
		String deleteSql = "delete from " + TABLE_NAME + " where collection_type_update_time < " + timeToRemove + " AND collection_type = " + AlertsCollectionType.Daily.toLong();
		dbUtils.executeUpdate(deleteSql);
	}


	@Override
	public String toString() {
		return "NotificationsMysqlConnector [dbUtils=" + dbUtils + "]";
	}

	
}
