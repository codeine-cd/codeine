package codeine.db.mysql;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection implements IDBConnection {
    private int TIMEOUT_SEC = 5;
    private static final Logger log = Logger.getLogger(DBConnection.class);

    @Override
    public void closeConnection(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            log.debug("fail to close connection");
        }
    }

    @Override
    public boolean checkConnection(String host, Integer port, String user, String password) {
        String url = "jdbc:mysql://" + host + ":" + port + "/" + MysqlConstants.DB_NAME + "?socketTimeout=10000&connectTimeout=10000";
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, user, password);
            return connection.isValid(TIMEOUT_SEC);
        } catch (SQLException e) {
            log.warn("error while testing connection to " + url + " " + e.getMessage());
            return false;
        } finally {
            closeConnection(connection);
        }

    }
}