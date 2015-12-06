package codeine.db.mysql;

import codeine.jsons.global.MysqlConfigurationJson;
import org.apache.log4j.Logger;

import java.util.List;


public class NearestHostSelector {

	private static final Logger log = Logger.getLogger(NearestHostSelector.class);
	public static long DIFF_THRESHOLD = 250;
	private MysqlConnectionWithPing lastSql;
    private IMysqlConnectionsProvider mysqlHostsProvider;

	public NearestHostSelector(IMysqlConnectionsProvider mysqlHostsProvider) {
        log.info("Creating NearestHostSelector, DIFF_THRESHOLD=" + DIFF_THRESHOLD);
        this.mysqlHostsProvider = mysqlHostsProvider;
	}

	public MysqlConfigurationJson select() {
        List<MysqlConnectionWithPing> connectionsList = mysqlHostsProvider.getMysqlConnections();
        if (connectionsList.size() == 0) {
            throw new RuntimeException("no host is reachable");
        }
        MysqlConnectionWithPing fastestConnection = connectionsList.get(0);
        if (lastSql == null) {
            lastSql = fastestConnection;
            log.info("Setting first sql database " + lastSql.getConfiguration());
        }
        else {
            lastSql = getConnection(connectionsList, lastSql.getConfiguration());
            if (lastSql == null || fastestConnection.getPingTime() + DIFF_THRESHOLD < lastSql.getPingTime()) {
                log.info("Switching databases, new connection is " + fastestConnection + " last sql was " + lastSql);
                log.info("total diff: " + (lastSql.getPingTime() - fastestConnection.getPingTime()));
                lastSql = fastestConnection;
            }
        }
        log.info("Selected database: " + lastSql);
        return lastSql.getConfiguration();
	}

    private MysqlConnectionWithPing getConnection(List<MysqlConnectionWithPing> list, MysqlConfigurationJson configuration) {
        for (MysqlConnectionWithPing connection : list) {
            if (connection.getConfiguration().equals(configuration)) {
                return connection;
            }
        }
        return null;
    }
}
