package codeine.db.mysql;

import java.sql.Connection;

/**
 * Created by rezra3 on 10/19/15.
 */
public interface IDBConnection {
    void closeConnection(Connection connection);

    boolean checkConnection(String host, Integer port, String user, String password);
}
