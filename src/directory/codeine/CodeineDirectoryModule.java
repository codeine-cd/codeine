package codeine;

import javax.inject.Inject;
import javax.inject.Provider;

import codeine.configuration.ConfigurationReadManagerServer;
import codeine.configuration.IConfigurationManager;
import codeine.db.IAlertsDatabaseConnector;
import codeine.db.IStatusDatabaseConnector;
import codeine.db.ProjectsConfigurationConnector;
import codeine.db.mysql.MysqlHostSelector;
import codeine.db.mysql.NearestMysqlHostSelectorPreferLocalhost;
import codeine.db.mysql.StaticMysqlHostSelector;
import codeine.db.mysql.connectors.AlertsMysqlConnector;
import codeine.db.mysql.connectors.ProjectsConfigurationMysqlConnector;
import codeine.db.mysql.connectors.StatusMysqlConnector;
import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.jsons.global.MysqlConfigurationJson;
import codeine.jsons.peer_status.PeersProjectsStatus;
import codeine.jsons.peer_status.PeersProjectsStatusInDirectory;
import codeine.version.VersionsMapping;
import codeine.version.VersionsMappingProvider;
import codeine.version.VersionsMappingStore;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class CodeineDirectoryModule extends AbstractModule
{
	

	@Override
	protected void configure()
	{
		bind(IAlertsDatabaseConnector.class).to(AlertsMysqlConnector.class);
		bind(ProjectsConfigurationConnector.class).to(ProjectsConfigurationMysqlConnector.class);
		bind(IStatusDatabaseConnector.class).to(StatusMysqlConnector.class);
		bind(MysqlHostSelector.class).toProvider(MysqlHostSelectorProvider.class).in(Scopes.SINGLETON);
		bind(PeersProjectsStatus.class).to(PeersProjectsStatusInDirectory.class).in(Scopes.SINGLETON);
		bind(VersionsMappingStore.class).in(Scopes.SINGLETON);
		bind(VersionsMapping.class).toProvider(VersionsMappingProvider.class).in(Scopes.SINGLETON);
		bind(IConfigurationManager.class).to(ConfigurationReadManagerServer.class).in(Scopes.SINGLETON);
	}

	public static class MysqlHostSelectorProvider implements Provider<MysqlHostSelector> {

		@Inject private GlobalConfigurationJsonStore conf;

		@Override
		public MysqlHostSelector get() {
			MysqlConfigurationJson localConfOrNull = NearestMysqlHostSelectorPreferLocalhost.getLocalConfOrNull(conf);
			if (null == localConfOrNull) {
				throw new RuntimeException("could not find db conf to start with");
			}
			return new StaticMysqlHostSelector(localConfOrNull);
		}
		
	}
}
