package codeine.db.mysql.connectors;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.db.IStatusDatabaseConnector;
import codeine.db.mysql.DbUtils;
import codeine.jsons.peer_status.PeerStatusJsonV2;
import codeine.utils.ExceptionUtils;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

public class StatusMysqlConnector implements IStatusDatabaseConnector{
	private static final Logger log = Logger
			.getLogger(StatusMysqlConnector.class);
	@Inject
	private DbUtils dbUtils;
	@Inject
	private Gson gson;

	private String tableName = "ProjectStatusList";
	public void createTables() {
		String colsDefinition = "peer_key VARCHAR(150) NOT NULL PRIMARY KEY, data text, update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, status VARCHAR(50) DEFAULT 'On' NOT NULL";
		dbUtils.executeUpdate("create table if not exists " + tableName + " (" + colsDefinition + ")");
	}

	@Override
	public void putReplaceStatus(PeerStatusJsonV2 p) {
		String json = gson.toJson(p);
		log.info("will update status:\n"+json);
		dbUtils.executeUpdate("REPLACE INTO "+tableName+" (peer_key, data, update_time ) VALUES (?, ?, CURRENT_TIMESTAMP())", p.peer_key(), json);
	}
	
	@Override
	public Map<String, PeerStatusJsonV2> getPeersStatus() {
		final Map<String, PeerStatusJsonV2> $ = Maps.newHashMap();
		Function<ResultSet, Void> function = new Function<ResultSet, Void>() {
			@Override
			public Void apply(ResultSet rs){
				try {
					String key = rs.getString(1);
					String value = rs.getString(2);
					$.put(key, gson.fromJson(value, PeerStatusJsonV2.class));
					return null;
				} catch (SQLException e) {
					throw ExceptionUtils.asUnchecked(e); 
				}
			}
		};
		dbUtils.executeQuery("select * from " + tableName, function);
		return $;
	}

	@Override
	public int removeExpiredPeers(int timeToLive) {
		return dbUtils.executeUpdate("DELETE FROM " + tableName + " WHERE  TIMESTAMPDIFF(MINUTE,update_time,CURRENT_TIMESTAMP()) > " + timeToLive);
	}

	@Override
	public int updatePeerStatusToDisconnected(int timeToLive) {
		return dbUtils.executeUpdate("UPDATE " + tableName + " SET status = 'Disc' WHERE  TIMESTAMPDIFF(MINUTE,update_time,CURRENT_TIMESTAMP()) >" + timeToLive);
	}
}
