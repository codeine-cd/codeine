package codeine.db.mysql;

import javax.inject.Inject;

import codeine.db.mysql.connectors.AlertsMysqlConnector;
import codeine.db.mysql.connectors.ProjectsConfigurationMysqlConnector;
import codeine.db.mysql.connectors.StatusMysqlConnector;

public class MysqlDatabaseSchemaManagement {

	@Inject
	private DbUtils dbUtils;
	@Inject
	private StatusMysqlConnector statusMysqlConnector;
	@Inject
	private AlertsMysqlConnector alertMysqlConnector;
	@Inject
	private ProjectsConfigurationMysqlConnector projectsConfiguratioConnector;
	
	public void initDatabase() {
		try {
			dbUtils.executeUpdate("CREATE DATABASE IF NOT EXISTS " + MysqlConstants.DB_NAME);
			createTables();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private void createTables() {
		statusMysqlConnector.createTables();
		alertMysqlConnector.createTables();
		projectsConfiguratioConnector.createTables();
	}
}
