package codeine.db.mysql;

import codeine.jsons.global.MysqlConfigurationJson;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MysqlConnectionsProvider implements IMysqlConnectionsProvider {

    private static final Logger log = Logger.getLogger(MysqlConnectionsProvider.class);
    private List<MysqlConfigurationJson> sqlHosts;
    @Inject
    private IDBConnection dbConnection;

    public MysqlConnectionsProvider(List<MysqlConfigurationJson> sqls) {
        sqlHosts = sqls;
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
        Collections.sort($, new Comparator<MysqlConnectionWithPing>() {
            @Override
            public int compare(MysqlConnectionWithPing o1, MysqlConnectionWithPing o2) {
                return Long.compare(o1.getPingTime(),o2.getPingTime());
            }
        });
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
