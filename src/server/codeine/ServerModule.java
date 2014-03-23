package codeine;


import codeine.CodeineDirectoryModule.MysqlHostSelectorProvider;
import codeine.command_peer.NodesCommandExecuterProvider;
import codeine.configuration.IConfigurationManager;
import codeine.db.IAlertsDatabaseConnector;
import codeine.db.IStatusDatabaseConnector;
import codeine.db.ProjectsConfigurationConnector;
import codeine.db.mysql.MysqlHostSelector;
import codeine.db.mysql.connectors.AlertsMysqlConnector;
import codeine.db.mysql.connectors.ProjectsConfigurationMysqlConnector;
import codeine.db.mysql.connectors.StatusMysqlConnector;
import codeine.jsons.peer_status.PeersProjectsStatus;
import codeine.jsons.peer_status.PeersProjectsStatusInWebServer;
import codeine.servlet.PrepareForShutdown;
import codeine.servlet.UsersManager;
import codeine.statistics.IMonitorStatistics;
import codeine.statistics.MonitorsStatisticsProvider;
import codeine.version.VersionsMapping;
import codeine.version.VersionsMappingProvider;
import codeine.version.VersionsMappingStore;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class ServerModule extends AbstractModule
{
	
	@Override
	protected void configure()
	{
		bind(IAlertsDatabaseConnector.class).to(AlertsMysqlConnector.class);
		bind(ProjectsConfigurationConnector.class).to(ProjectsConfigurationMysqlConnector.class);
		bind(IStatusDatabaseConnector.class).to(StatusMysqlConnector.class);
		bind(MysqlHostSelector.class).toProvider(MysqlHostSelectorProvider.class).in(Scopes.SINGLETON);
		bind(PeersProjectsStatus.class).to(PeersProjectsStatusInWebServer.class).in(Scopes.SINGLETON);
		bind(ConfigurationManagerServer.class).in(Scopes.SINGLETON);
		bind(IConfigurationManager.class).to(ConfigurationManagerServer.class);
		bind(ProjectConfigurationInPeerUpdater.class).in(Scopes.SINGLETON);
		bind(UsersManager.class).in(Scopes.SINGLETON);
		bind(NodesCommandExecuterProvider.class).in(Scopes.SINGLETON);
		bind(IMonitorStatistics.class).toProvider(MonitorsStatisticsProvider.class).in(Scopes.SINGLETON);
		bind(VersionsMappingStore.class).in(Scopes.SINGLETON);
		bind(VersionsMapping.class).toProvider(VersionsMappingProvider.class).in(Scopes.SINGLETON);
		bind(PrepareForShutdown.class).in(Scopes.SINGLETON);
	}
	
}
