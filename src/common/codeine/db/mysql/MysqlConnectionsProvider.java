package codeine.db.mysql;

import codeine.jsons.global.MysqlConfigurationJson;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.log4j.Logger;

public class MysqlConnectionsProvider implements IMysqlConnectionsProvider {

    private static final Logger log = Logger.getLogger(MysqlConnectionsProvider.class);
    private List<MysqlConfigurationJson> sqlHosts;
    private IDBConnection dbConnection;

    public MysqlConnectionsProvider(List<MysqlConfigurationJson> sqls, IDBConnection dbConnection) {
        sqlHosts = sqls;
        this.dbConnection = dbConnection;
    }

    @Override
    public List<MysqlConnectionWithPing> getMysqlConnections() {
        List<MysqlConnectionWithPing> $ = Lists.newArrayList();
        for (MysqlConfigurationJson sqlHost : sqlHosts) {
            long time = check(sqlHost);
            if (time != Long.MAX_VALUE) {
                $.add(new MysqlConnectionWithPing(sqlHost, check(sqlHost)));
            }
        }
        Collections.sort($, Comparator.comparingLong(MysqlConnectionWithPing::getPingTime));
        return $;
    }

    private long check(MysqlConfigurationJson mysql) {
        long start = System.currentTimeMillis();
        if (!dbConnection.checkConnection(mysql.host(), mysql.port(), mysql.user(), mysql.password())){
            log.info("Failed to check connection to " + mysql);
            return Long.MAX_VALUE;
        }
        return  System.currentTimeMillis() - start;
    }
}
