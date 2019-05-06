package codeine.db.mysql;

import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.jsons.global.MysqlConfigurationJson;
import codeine.utils.exceptions.ConnectToDatabaseException;
import codeine.utils.exceptions.DatabaseException;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import javax.inject.Inject;
import org.apache.log4j.Logger;

public class DbUtils {

    private Map<String, HikariDataSource> hikariDataSourceMap = Maps.newHashMap();
    private static Logger log = Logger.getLogger(DbUtils.class);
    private DBConnection dbConnection = new DBConnection();

    private MysqlHostSelector hostSelector;
    private GlobalConfigurationJsonStore globalConfigurationJsonStore;
    private final HealthCheckRegistry healthCheckRegistry;
    private final MetricRegistry metricRegistry;

    @Inject
    public DbUtils(MysqlHostSelector hostSelector, GlobalConfigurationJsonStore globalConfigurationJsonStore,
        HealthCheckRegistry healthCheckRegistry, MetricRegistry metricRegistry) {
        super();
        this.hostSelector = hostSelector;
        this.globalConfigurationJsonStore = globalConfigurationJsonStore;
        this.healthCheckRegistry = healthCheckRegistry;
        this.metricRegistry = metricRegistry;
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

    private void executeQuery(String sql, Function<ResultSet, Void> function, boolean root, boolean useCompression) {
        ResultSet rs = null;
        PreparedStatement preparedStatement = null;
        Connection connection = root ? getConnectionForRoot(useCompression) : getConnection(useCompression);
        try {
            preparedStatement = connection.prepareStatement(sql);
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                function.apply(rs);
            }
        } catch (SQLException e) {
            log.error("Error during executeQuery", e);
            throw prepareException(sql, connection, e, null);
        } finally {
            closeStatement(preparedStatement);
            closeResultSet(rs);
            dbConnection.closeConnection(connection);
        }
    }

    private DatabaseException prepareException(String sql, Connection connection, SQLException e, String[] args) {
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
        String jdbcUrl = "jdbc:mysql://" + mysqlAddress + "/" + MysqlConstants.DB_NAME + "?useCompression=true";
        try {
            if (globalConfigurationJsonStore.get().large_deployment()) {
                return DriverManager
                    .getConnection(jdbcUrl, hostSelector.mysql().user(), hostSelector.mysql().password());
            } else {
                return getDBConnection(jdbcUrl, hostSelector.mysql());
            }
        } catch (SQLException e) {
            throw new ConnectToDatabaseException(jdbcUrl, e);
        }
    }

    private Connection getConnectionForRoot(boolean useCompression) {
        String url =
            "jdbc:mysql://localhost:" + hostSelector.mysql().port() + "/" + MysqlConstants.DB_NAME + "?user=root&"
                + "createDatabaseIfNotExist=true";
        if (useCompression) {
            url += "&useCompression=true";
        }
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new ConnectToDatabaseException(url, e);
        }
    }

    private Connection getDBConnection(String sqlAddress, MysqlConfigurationJson mysql) throws SQLException {
        HikariDataSource dataSource = hikariDataSourceMap.computeIfAbsent(sqlAddress, address -> {
            HikariConfig config = new HikariConfig();
            config.setUsername(mysql.user());
            config.setPassword(mysql.password());
            config.setJdbcUrl(address);
            config.setPoolName(mysql.host());
            config.setMaximumPoolSize(globalConfigurationJsonStore.get().max_db_pool_size());
            config.setMinimumIdle(globalConfigurationJsonStore.get().min_db_pool_size());
            config.setConnectionTimeout(60000);
            config.addDataSourceProperty("cachePrepStmts", true);
            config.addDataSourceProperty("prepStmtCacheSize", 250);
            config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
            config.addDataSourceProperty("useServerPrepStmts", true);
            config.addDataSourceProperty("useLocalSessionState", true);
            config.addDataSourceProperty("useLocalTransactionState", true);
            config.addDataSourceProperty("rewriteBatchedStatements", true);
            config.addDataSourceProperty("cacheResultSetMetadata", true);
            config.addDataSourceProperty("cacheServerConfiguration", true);
            config.addDataSourceProperty("elideSetAutoCommits", true);
            config.addDataSourceProperty("maintainTimeStats", false);
//            config.setMetricRegistry(metricRegistry);
            config.setHealthCheckRegistry(healthCheckRegistry);
            config.addHealthCheckProperty("connectivityCheckTimeoutMs",
                globalConfigurationJsonStore.get().connectivity_check_timeout_ms().toString());
            return new HikariDataSource(config);
        });
        return dataSource.getConnection();
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
