package codeine.db.mysql;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.db.mysql.connectors.AlertsMysqlConnector;
import codeine.db.mysql.connectors.ProjectsConfigurationMysqlConnector;
import codeine.db.mysql.connectors.StatusMysqlConnector;
import codeine.executer.PeriodicExecuter;
import codeine.executer.Task;
import codeine.jsons.global.MysqlConfigurationJson;
import codeine.utils.ExceptionUtils;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class MysqlProcessControlService {
	private static final long MYSQL_CHECK_INTERVAL = TimeUnit.HOURS.toMillis(1);

	public static boolean CHECK_TABLE_INTEGRITY_ON_STARTUP = false;

	transient private static Logger log = Logger.getLogger(MysqlProcessControlService.class.getName());
	private MXJController m_mysql = null;
	private MXJController m_slaveMysql = null;
	private MysqlProcessConfiguration m_conf = null;

	/**
	 * when true, set global transaction isolation level to READ UNCOMMITED to
	 * prevent locking
	 */
	private boolean m_bNoLock = false;
	private String m_sSlavePath;

	@Inject
	private DbUtils dbUtils;
	@Inject
	private StatusMysqlConnector statusMysqlConnector;
	@Inject
	private AlertsMysqlConnector alertMysqlConnector;
	@Inject
	private ProjectsConfigurationMysqlConnector projectsConfiguratioConnector;
	@Inject
	private MysqlHostSelector mysqlHostSelector;

	public MysqlProcessControlService() {
	}

	public static void main(String[] args) {
		MysqlProcessControlService m = new MysqlProcessControlService();
		m.config();
		m.startServers();

	}

	public void stopServers() {
		m_mysql.stop();
		if (m_slaveMysql != null) {
			m_slaveMysql.stop();
		}
	}

	public void config() {
		MysqlConfigurationJson mysqlConf = mysqlHostSelector.getLocalConf();
		String dir = mysqlConf.dir();
		log.info("config() - path for persistency " + dir);
		if (new File(dir).mkdirs()) {
			log.info("config() - created directory " + dir);
		}
		if (m_sSlavePath == null) {
			m_mysql = new MXJController(dir, mysqlConf.port(), mysqlConf.bin_dir());
		} else {
			m_mysql = new MXJController(dir, getFreePort(), mysqlConf.bin_dir());
			m_mysql.setEnableBinaryLog(true);
			m_mysql.setServerID(1);
			m_slaveMysql = new MXJController(m_sSlavePath, getFreePort(), mysqlConf.bin_dir());
			m_slaveMysql.setServerID(2);
		}
		if (null != m_conf) {
			m_mysql.addOptions(m_conf.getSqlOptions());
		}
		System.setProperty("mysql.port", String.valueOf(m_mysql.getPort()));
	}

	// @Override
	// public String getSlavePath()
	// {
	// return m_slaveMysql == null ? null : m_slaveMysql.getBaseDir();
	// }
	//
	// @Override
	// public void setConfiguration(MysqlProcessConfiguration conf)
	// {
	// m_conf = conf;
	// }

	// public static void main(String[] args) throws UnknownHostException {
	// System.out.println(InetUtils.getLocalHost().equals(InetAddress.getByName("itstl1058.iil.intel.com")));
	// }
	private int getFreePort() {
		try {
			ServerSocket ssocket = new ServerSocket(0);
			int port = ssocket.getLocalPort();
			ssocket.close();
			return port;
		} catch (IOException e) {
			log.error("getFreePort() - I/O Exception", e);
			return 0;
		}

	}

	public void startServers() {
		startDatabase();
		if (!m_mysql.isRunning()) {
			log.warn("mysqld PID not found, will attempt to restart mysqld");
			m_mysql.stop();
			startDatabase();
		}
	}

	private void startDatabase() {
		startMysqlServer();
		initDatabase();
		chmodDBPermissions();
	}

	private void initDatabase() {
		try {
			dbUtils.executeUpdateAsRoot("CREATE DATABASE IF NOT EXISTS " + MysqlConstants.DB_NAME);
			if (isNoLock()) {
				dbUtils.executeUpdateAsRoot("SET GLOBAL TRANSACTION ISOLATION LEVEL READ UNCOMMITTED");
			}
			boolean exists = createDbUser();
			if (!exists) {
				createTables();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void createTables() {
		statusMysqlConnector.createTables();
		alertMysqlConnector.createTables();
		projectsConfiguratioConnector.createTables();
	}

	private boolean createDbUser() throws SQLException {
		String sql = "SELECT user FROM mysql.user WHERE user='" + MysqlConstants.DB_USER + "'";
		final List<String> result = Lists.newArrayList();
		Function<ResultSet, Void> function = new Function<ResultSet, Void>() {
			@Override
			public Void apply(ResultSet rs) {
				try {
					result.add(rs.getString(1));
					return null;
				} catch (SQLException e) {
					throw ExceptionUtils.asUnchecked(e);
				}
			}
		};
		dbUtils.executeQueryAsRoot(sql, function);
		if (!result.isEmpty() && !(result.contains(MysqlConstants.DB_USER))) {
			log.info("createDbUser() - user already exists " + MysqlConstants.DB_USER);
			return true;
		} else {
			log.info("createDbUser() - creating user in database");
			sql = "CREATE USER '" + MysqlConstants.DB_USER + "'@'%' identified by '" + MysqlConstants.DB_PASSWORD + "'";
			int rs = dbUtils.executeUpdateAsRoot(sql);
			if (rs != -1) {
				log.info("user created successfully");
			}
			sql = "GRANT ALL PRIVILEGES ON *.* TO '" + MysqlConstants.DB_USER + "'@'%' WITH GRANT OPTION";
			rs = dbUtils.executeUpdateAsRoot(sql);
			if (rs != -1) {
				log.info("permissions for the user created successfully");
			}
			return false;
		}
	}

	/**
	 * change the database file permissions in the file system to make it
	 * readable to group level (like all other files). By default, MySQL creates
	 * it with user permissions only
	 */
	private void chmodDBPermissions() {
		// FileUtil.chmod(m_mysql.getDataDir(), "g+r", true);
	}

	// private void registerMysqlConnection()
	// {
	// DatabaseConnectionData.Builder builder = new
	// DatabaseConnectionData.Builder(DatabaseTypes.MYSQL_MXJ);
	// builder.setUsername(DatabaseConnectionData.DB_USER_NAME);
	// builder.setDatabaseName(DB_NAME);
	// ServiceLocator.serviceOf(IDBAccessService.class).createConnection(CONNECTION_ID,
	// builder.build());
	// }

	private void startMysqlServer() {
		if (!m_mysql.start()) {
			throw new RuntimeException("Cannot start mysql");
		}
		if (m_slaveMysql != null) {
			if (m_slaveMysql.start()) {
				try {
					// setupReplication();
				} catch (Exception e) {
					log.error("Failed to set repolication", e);
				}
			}
		}
	}

	// private void setupReplication() throws SQLException
	// {
	// ReplicationSetupResult result = setupReplicationOnMaster();
	// (new ReplicationCleanupTask()).startAfterWait();
	// setupReplicationSlave(result.logFileName, result.logPosition);
	// }
	//
	// private class ReplicationSetupResult
	// {
	// public String logFileName;
	// public long logPosition;
	// }

	// private ReplicationSetupResult setupReplicationOnMaster() throws
	// SQLException
	// {
	// DatabaseConnectionData.Builder builder = new
	// DatabaseConnectionData.Builder(DatabaseTypes.MYSQL_MXJ);
	// builder.setUsername(DatabaseConnectionData.DB_SUPER_USER_NAME);
	// builder.setDatabaseName(null);
	// builder.setDatabasePort(m_mysql.getPort());
	// IDatabaseConnectionManager dbconn =
	// ServiceLocator.serviceOf(IDBAccessService.class).createConnection(CONNECTION_ID,
	// builder.build());
	// if(!dbconn.isConnected())
	// {
	// log.error("Could not connect to database (2) check mysql log for more details "
	// + PersistencyData.getDataStoragePath() + "/data/mysql.out");
	// throw new SQLException("Cannot connect to master DB");
	// }
	// createDbUser(DB_REPLICATION_USER_NAME, "REPLICATION SLAVE");
	// DbUtils.executeStatement("FLUSH TABLES WITH READ LOCK",CONNECTION_ID);
	// final ReplicationSetupResult result = new ReplicationSetupResult();
	// Function<IDBRow,String> function = new Function<IDBRow,String>()
	// {
	// @Override
	// public String apply(IDBRow from)
	// {
	// result.logFileName = from.getString("File");
	// result.logPosition = from.getLong("Position");
	// return null;
	// }
	// };
	//
	// ServiceLocator.serviceOf(IDBAccessService.class).executeQuery("SHOW MASTER STATUS",
	// CONNECTION_ID,function);
	// ServiceLocator.serviceOf(IDBAccessService.class).destroyConnection(CONNECTION_ID);
	// return result;
	// }

	// private void setupReplicationSlave(String logFileName, long
	// logFilePosition) throws SQLException
	// {
	// DatabaseConnectionData.Builder builder = new
	// DatabaseConnectionData.Builder(DatabaseTypes.MYSQL_MXJ);
	// builder.setUsername(DatabaseConnectionData.DB_SUPER_USER_NAME);
	// builder.setDatabaseName(null);
	// builder.setDatabasePort(m_slaveMysql.getPort());
	// IDatabaseConnectionManager conn =
	// ServiceLocator.serviceOf(IDBAccessService.class).createConnection(CONNECTION_ID,
	// builder.build());
	// if(conn==null)
	// {
	// log.error("Could not connect to database (2) check mysql log for more details "
	// + PersistencyData.getDataStoragePath() + "/data/mysql.out");
	// throw new SQLException("Cannot connect to slave DB");
	// }
	// DbUtils.executeStatement("STOP SLAVE", CONNECTION_ID);
	// createDbUser(DB_REPLICATION_USER_NAME, "REPLICATION SLAVE");
	// DbUtils.executeStatement("CHANGE MASTER TO MASTER_HOST='localhost', MASTER_PORT="
	// + m_mysql.getPort() + ", MASTER_USER='" + DB_REPLICATION_USER_NAME
	// + "', MASTER_LOG_FILE='" + logFileName + "', MASTER_LOG_POS=" +
	// logFilePosition, CONNECTION_ID);
	// DbUtils.executeStatement("START SLAVE", CONNECTION_ID);
	// ServiceLocator.serviceOf(IDBAccessService.class).destroyConnection(CONNECTION_ID);
	// }

	public boolean isNoLock() {
		return m_bNoLock;
	}

	public void setNoLock(boolean noLock) {
		m_bNoLock = noLock;
	}

	public void execute() {
		config();
		startServers();
		new PeriodicExecuter(MYSQL_CHECK_INTERVAL, new MySQLProcessMonitor(m_mysql), "mysql_monitor").runInThread();
	}

	// private class ReplicationCleanupTask extends RecurringTask
	// {
	//
	// public ReplicationCleanupTask()
	// {
	// super(DB_REPLICATION_CLEANUP_MILLI);
	// }
	//
	// @Override
	// public boolean performTask(Object context)
	// {
	// try
	// {
	// log.info("replication logs cleaned up");
	// DbUtils.executeStatement("RESET MASTER", CONNECTION_ID);
	// m_slaveMysql.cleanReplicationLogs();
	// }
	// catch (SQLException e)
	// {
	// log.warn("cannot cleanup replication logs", e);
	// }
	// return true;
	// }
	//
	// @Override
	// public void onTaskFailure(Object context)
	// {
	// }
	// }

	private class MySQLProcessMonitor implements Task {
		private MXJController m_controller;

		MySQLProcessMonitor(MXJController controller) {
			super();
			m_controller = controller;
		}

		private void check() {
			log.debug("checking if mysqld is running");
			if (!m_controller.isRunning()) {
				log.warn("mysqld PID not found, will attempt to restart mysqld");
				m_controller.stop();
				startDatabase();
			}

		}

		@Override
		public void run() {
			check();
		}
	}

}
