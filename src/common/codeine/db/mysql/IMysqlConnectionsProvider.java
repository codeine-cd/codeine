package codeine.db.mysql;

import java.util.List;

public interface IMysqlConnectionsProvider {
    List<MysqlConnectionWithPing> getMysqlConnections();
}
