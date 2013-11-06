package codeine.db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.jsons.global.GlobalConfigurationJson;
import codeine.utils.ExceptionUtils;

import com.google.common.base.Function;

public class DbUtils
{
	private static Logger log = Logger.getLogger(DbUtils.class);
	private static int TIMEOUT_SEC = 5; 
	
	@Inject
	private GlobalConfigurationJson conf;
	@Inject
	private MysqlHostSelector hostSelector;
	
	public static void closeStatement(Statement stmt)
	{
		if(stmt != null)
		{
			try
			{
				stmt.close();
			}
			catch(SQLException e)
			{
				log.debug("closeStatement() - failed to close stmt", e);
			}
		}
	}

	public static Statement closeResultSet(ResultSet rs)
	{
		Statement stmt = null;
		if(rs != null)
		{
			try
			{
				stmt = rs.getStatement();
				rs.close();
			}
			catch(SQLException e)
			{
				log.debug("closeResultSet() - failed to close rs", e);
			}
		}
		return stmt;
	}

	public void executeQueryAsRoot(String sql, Function<ResultSet, Void> function) {
		executeQuery(sql, function, true);
	}
	public void executeQuery(String sql, Function<ResultSet, Void> function){
		executeQuery(sql, function, false);
	}
	private void executeQuery(String sql, Function<ResultSet, Void> function, boolean root)
	{
		ResultSet rs = null;
		Connection connection = root ? getConnectionForRoot() : getConnection();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			rs = preparedStatement.executeQuery();
			while (rs.next()) {
				function.apply(rs);
			}
		} catch (SQLException e) {
			throw ExceptionUtils.asUnchecked(e);
		} finally {
			closeResultSet(rs);
			closeConnection(connection);
		}
	}
	public void executeUpdateableQuery(String sql, Function<ResultSet, Void> function)
	{
		ResultSet rs = null;
		Connection connection = getConnection();
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			rs = preparedStatement.executeQuery();
			while (rs.next()) {
				function.apply(rs);
			}
		} catch (SQLException e) {
			throw ExceptionUtils.asUnchecked(e);
		} finally {
			closeResultSet(rs);
			closeConnection(connection);
		}
	}

	public int executeUpdate(String sql, String... args) {
		return executeUpdate(sql, false, args);
	}
	private int executeUpdate(String sql, boolean root, String... args) {
		PreparedStatement preparedStatement = null;
		Connection connection = root ? getConnectionForRoot() : getConnection();
		try {
			preparedStatement = connection.prepareStatement(sql);
			for (int i = 1; i <= args.length; i++) {
				preparedStatement.setString(i, args[i-1]);
			}
			return preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw ExceptionUtils.asUnchecked(e);
		} finally {
			closeStatement(preparedStatement);
			closeConnection(connection);
		}
	}
	
	public int executeUpdateAsRoot(String sql) {
		return executeUpdate(sql, true);
	}

	private static void closeConnection(Connection connection) {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			log.debug("fail to close connection");
		}
	}

	private Connection getConnection() {
		String url = "jdbc:mysql://"+hostSelector.mysql().host()+":" + hostSelector.mysql().port() + "/" + MysqlConstants.DB_NAME;
		try {
			return DriverManager.getConnection(url, MysqlConstants.DB_USER, MysqlConstants.DB_PASSWORD);
		} catch (SQLException e) {
			throw ExceptionUtils.asUnchecked(e);
		}
	}
	private Connection getConnectionForRoot() {
		String url = "jdbc:mysql://localhost:" + hostSelector.mysql().port() + "/" + MysqlConstants.DB_NAME + "?user=root&" + "createDatabaseIfNotExist=true";
		try {
			return DriverManager.getConnection(url);
		} catch (SQLException e) {
			throw ExceptionUtils.asUnchecked(e);
		}
	}
	
	public static boolean checkConnection(String host, Integer port) {
		String url = "jdbc:mysql://"+host+":" + port + "/" + MysqlConstants.DB_NAME;
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(url, MysqlConstants.DB_USER, MysqlConstants.DB_PASSWORD);			
			return connection.isValid(TIMEOUT_SEC);
		} catch (SQLException e) {
			log.warn("error while testing connection to " + url + " " + e.getMessage());
			return false;
		}
		finally {
			closeConnection(connection);
		}
		
	}

}
