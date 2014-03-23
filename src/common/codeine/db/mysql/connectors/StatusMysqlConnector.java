package codeine.db.mysql.connectors;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.db.IStatusDatabaseConnector;
import codeine.db.mysql.DbUtils;
import codeine.jsons.global.ExperimentalConfJsonStore;
import codeine.jsons.peer_status.PeerStatusJsonV2;
import codeine.jsons.peer_status.PeerStatusString;
import codeine.jsons.peer_status.PeerType;
import codeine.utils.ExceptionUtils;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

public class StatusMysqlConnector implements IStatusDatabaseConnector{
	private static final Logger log = Logger.getLogger(StatusMysqlConnector.class);
	@Inject
	private DbUtils dbUtils;
	@Inject
	private Gson gson;
	@Inject	private ExperimentalConfJsonStore webConfJsonStore;
	private String tableName = "ProjectStatusList";
	
	
	
	public StatusMysqlConnector() {
		super();
	}

	
	public StatusMysqlConnector(DbUtils dbUtils, Gson gson, ExperimentalConfJsonStore webConfJsonStore) {
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
		String colsDefinition = "peer_key VARCHAR(150) NOT NULL PRIMARY KEY, data text, update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, status VARCHAR(50) DEFAULT 'On' NOT NULL";
		dbUtils.executeUpdate("create table if not exists " + tableName + " (" + colsDefinition + ")");
	}

	@Override
	public void putReplaceStatus(PeerStatusJsonV2 p) {
		String json = gson.toJson(p);
		log.info("will update status:\n"+json);
		dbUtils.executeUpdate("DELETE FROM "+tableName+" WHERE peer_key = '" + p.peer_old_key() + "'");
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
					PeerStatusJsonV2 peerStatus = gson.fromJson(value, PeerStatusJsonV2.class);
					peerStatus.status(PeerStatusString.valueOf(rs.getString("status")));
					updateNodesWithPeer(peerStatus);
					$.put(key, peerStatus);
					return null;
				} catch (SQLException e) {
					throw ExceptionUtils.asUnchecked(e); 
				}
			}

		};
		dbUtils.executeQuery("select * from " + tableName, function);
		return $;
	}
	
	private void updateNodesWithPeer(PeerStatusJsonV2 peerStatus) {
		peerStatus.updateNodesWithPeer();
	}
	
	@Override
	public void updatePeersStatus(final long timeToRemove, final long timeToDisc) {
		final List<String> idToRemove = Lists.newArrayList();
		final List<String> idToDisc = Lists.newArrayList();
		Function<ResultSet, Void> function = new Function<ResultSet, Void>() {
			@Override
			public Void apply(ResultSet rs){
				try {
					String key = rs.getString("peer_key");
//					PeerStatusString status = PeerStatusString.valueOf(rs.getString("status"));
					String value = rs.getString("data");
					String status = rs.getString("status");
					PeerStatusJsonV2 peerStatus = gson.fromJson(value, PeerStatusJsonV2.class);
					PeerType peerType = peerStatus.peer_type();
					long timeToRemovePeer = peerType == PeerType.Reporter ? timeToRemove + TimeUnit.DAYS.toMinutes(7) : timeToRemove;
					long timeToDiscPeer = peerType == PeerType.Reporter ? timeToDisc + TimeUnit.DAYS.toMinutes(7) : timeToDisc;
					long timeDiff = rs.getLong("TIME_DIFF");
					log.debug("time diff is " + timeDiff);
					if (timeDiff > timeToRemovePeer){
						log.info("time diff is " + timeDiff);
						log.info("deleting " + peerStatus);
//						rs.deleteRow();
						idToRemove.add(key);
					}
					else if (timeDiff > timeToDiscPeer && !status.equals("Disc")){
						log.info("time diff is " + timeDiff);
						log.info("update to disc " + peerStatus);
						idToDisc.add(key);
//						rs.updateString("status", "Disc");
//						rs.updateRow();
					}
					return null;
				} catch (SQLException e) {
					throw ExceptionUtils.asUnchecked(e); 
				}
			}

		};
		dbUtils.executeUpdateableQuery("select *,TIMESTAMPDIFF(MINUTE,update_time,CURRENT_TIMESTAMP()) as TIME_DIFF from " + tableName, function);
		if (webConfJsonStore.get().readonly_web_server()) {
			log.info("read only mode");
			return;
		}
		for (String key : idToRemove) {
			log.info("deleting " + key);
			dbUtils.executeUpdate("DELETE from " + tableName + " WHERE peer_key = ?", key);
		}
		for (String key : idToDisc) {
			log.info("discing " + key);
			dbUtils.executeUpdate("UPDATE " + tableName + " SET status = 'Disc' WHERE peer_key = ?", key);
		}
	}
	
	
}
