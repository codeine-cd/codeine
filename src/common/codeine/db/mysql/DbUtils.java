package codeine.db.mysql;

import codeine.jsons.global.MysqlConfigurationJson;
import codeine.utils.exceptions.ConnectToDatabaseException;
import codeine.utils.exceptions.DatabaseException;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;
import javax.inject.Inject;
import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDriver;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.log4j.Logger;

public class DbUtils {

    private final GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
    private static Logger log = Logger.getLogger(DbUtils.class);
    private DBConnection dbConnection = new DBConnection();
    private Map<String, String> connectionsPool = Maps.newConcurrentMap();

    @Inject
    private MysqlHostSelector hostSelector;


    public DbUtils() {
        super();
        genericObjectPoolConfig.setMaxTotal(2);
        genericObjectPoolConfig.setMinIdle(1);
        genericObjectPoolConfig.setMaxIdle(2);try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            log.error("Failed to load MySQL JDBC driver", e);
        }
    }


    public DbUtils(MysqlHostSelector hostSelector) {
        this();
        this.hostSelector = hostSelector;
    }


    private static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.debug("closeStatement() - failed to close stmt", e);
            }
        }
    }

    private static Statement closeResultSet(ResultSet rs) {
        Statement stmt = null;
        if (rs != null) {
            try {
                stmt = rs.getStatement();
                rs.close();
            } catch (SQLException e) {
                log.debug("closeResultSet() - failed to close rs", e);
            }
        }
        return stmt;
    }

    public void executeQueryAsRoot(String sql, Function<ResultSet, Void> function) {
        executeQuery(sql, function, true, false);
    }

    public void executeQuery(String sql, Function<ResultSet, Void> function) {
        executeQuery(sql, function, false, false);
    }

    public void executeQueryCompressed(String sql, Function<ResultSet, Void> function) {
        executeQuery(sql, function, false, true);
    }

    private void executeQuery(String sql, Function<ResultSet, Void> function, boolean root,
        boolean useCompression) {
        ResultSet rs = null;
        PreparedStatement preparedStatement = null;
        Connection connection =
            root ? getConnectionForRoot(useCompression) : getConnection(useCompression);
        try {
            preparedStatement = connection.prepareStatement(sql);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                function.apply(rs);
            }
        } catch (SQLException e) {
            throw prepareException(sql, connection, e, null);
        } finally {
            closeStatement(preparedStatement);
            closeResultSet(rs);
            dbConnection.closeConnection(connection);
        }
    }

    private DatabaseException prepareException(String sql, Connection connection, SQLException e,
        String[] args) {
        try {
            return new DatabaseException(sql, connection.getMetaData().getURL(), e, args);
        } catch (SQLException e1) {
            return new DatabaseException(sql, "url could not be resolved", e, args);
        }
    }

    public void executeUpdateableQuery(String sql, Function<ResultSet, Void> function) {
        ResultSet rs = null;
        PreparedStatement preparedStatement = null;
        Connection connection = getConnection(false);
        try {
            preparedStatement = connection
                .prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                function.apply(rs);
            }
        } catch (SQLException e) {
            throw prepareException(sql, connection, e, null);
        } finally {
            closeStatement(preparedStatement);
            closeResultSet(rs);
            dbConnection.closeConnection(connection);
        }
    }

    public int executeUpdate(String sql, String... args) {
        return executeUpdate(sql, false, args);
    }

    private int executeUpdate(String sql, boolean root, String... args) {
        PreparedStatement preparedStatement = null;
        Connection connection = root ? getConnectionForRoot(true) : getConnection(true);
        try {
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 1; i <= args.length; i++) {
                preparedStatement.setString(i, args[i - 1]);
            }
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw prepareException(sql, connection, e, args);
        } finally {
            closeStatement(preparedStatement);
            dbConnection.closeConnection(connection);
        }
    }

    public int executeUpdateAsRoot(String sql) {
        return executeUpdate(sql, true);
    }

    private Connection getConnection(boolean useCompression) {
        String mysqlAddress = hostSelector.mysql().host() + ":" + hostSelector.mysql().port();
        String jdbcUrl =
            "jdbc:mysql://" + mysqlAddress + "/"
                + MysqlConstants.DB_NAME + "?connectTimeout=60000&socketTimeout=90000";
        if (useCompression) {
            jdbcUrl += "&useCompression=true";
        }
        String url = getConnectionString(jdbcUrl, hostSelector.mysql());
        try {
            return DriverManager
                .getConnection(url);
        } catch (SQLException e) {
            throw new ConnectToDatabaseException(url, e);
        }
    }

    private Connection getConnectionForRoot(boolean useCompression) {
        String url =
            "jdbc:mysql://localhost:" + hostSelector.mysql().port() + "/" + MysqlConstants.DB_NAME
                + "?user=root&" + "createDatabaseIfNotExist=true";
        if (useCompression) {
            url += "&useCompression=true";
        }
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new ConnectToDatabaseException(url, e);
        }
    }

    private String getConnectionString(String sqlAddress,
        MysqlConfigurationJson mysql) {
        return connectionsPool.computeIfAbsent(sqlAddress,
            s -> {
                ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(sqlAddress,
                    mysql.user(), mysql.password());
                PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(
                    connectionFactory, null);
                ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(
                    poolableConnectionFactory, genericObjectPoolConfig);
                poolableConnectionFactory.setPool(connectionPool);
                try {
                    Class.forName("org.apache.commons.dbcp2.PoolingDriver");
                    PoolingDriver driver = (PoolingDriver) DriverManager
                        .getDriver("jdbc:apache:commons:dbcp:");
                    String connectionPoolName = getConnectionPoolName(sqlAddress);
                    driver.registerPool(connectionPoolName, connectionPool);
                    log.info("Created connection pool to " + sqlAddress + " with name "
                        + connectionPoolName);
                    return "jdbc:apache:commons:dbcp:" + connectionPoolName;
                } catch (ClassNotFoundException | SQLException e) {
                    log.error("Failed to create connection pool, will use normal connections");
                    return "jdbc:mysql://" + sqlAddress + "/" + MysqlConstants.DB_NAME;
                }
            });
    }

    private Properties getConnectionProperties(boolean useCompression) {
        Properties props = new Properties();
        String propertiesTr = "connectTimeout=60000;socketTimeout=90000";
        if (useCompression) {
            propertiesTr += ";useCompression=true";
        }
        try {
            props.load(new StringReader(propertiesTr));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }

    private String getConnectionPoolName(String sqlAddress) {
        return sqlAddress.split(":")[0];
    }

    @Override
    public String toString() {
        return "DbUtils [" + server() + "]";
    }

    public String server() {
        MysqlConfigurationJson mysql = hostSelector.mysql();
        return mysql == null ? "null" : mysql.hostPort();
    }

}
