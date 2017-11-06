package codeine;

import codeine.configuration.ConfigurationReadManagerServer;
import codeine.configuration.IConfigurationManager;
import codeine.db.IStatusDatabaseConnector;
import codeine.db.ProjectsConfigurationConnector;
import codeine.db.mysql.MysqlHostSelector;
import codeine.db.mysql.NearestMysqlHostSelectorPreferLocalhost;
import codeine.db.mysql.StaticMysqlHostSelector;
import codeine.db.mysql.connectors.ProjectsConfigurationMysqlConnector;
import codeine.db.mysql.connectors.StatusMysqlConnector;
import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.jsons.global.MysqlConfigurationJson;
import codeine.version.VersionsMapping;
import codeine.version.VersionsMappingProvider;
import codeine.version.VersionsMappingStore;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import javax.inject.Inject;
import javax.inject.Provider;

public class CodeineDirectoryModule extends AbstractModule
{
	

	@Override
	protected void configure()
	{
		bind(ProjectsConfigurationConnector.class).to(ProjectsConfigurationMysqlConnector.class);
		bind(IStatusDatabaseConnector.class).to(StatusMysqlConnector.class);
		bind(MysqlHostSelector.class).toProvider(MysqlHostSelectorProvider.class).in(Scopes.SINGLETON);
		//bind(PeersProjectsStatus.class).to(PeersProjectsStatusInDirectory.class).in(Scopes.SINGLETON);
		bind(VersionsMappingStore.class).in(Scopes.SINGLETON);
		bind(VersionsMapping.class).toProvider(VersionsMappingProvider.class).in(Scopes.SINGLETON);
		bind(IConfigurationManager.class).to(ConfigurationReadManagerServer.class).in(Scopes.SINGLETON);
	}

	public static class MysqlHostSelectorProvider implements Provider<MysqlHostSelector> {

		@Inject private GlobalConfigurationJsonStore conf;

		@Override
		public MysqlHostSelector get() {
			MysqlConfigurationJson confOrNull;
			if (conf.get().disable_auto_select_mysql()) {
		 		confOrNull = conf.get().mysql().get(0);
			}
			else {
				confOrNull = NearestMysqlHostSelectorPreferLocalhost.getLocalConfOrNull(conf);
			}
			if (null == confOrNull) {
				throw new RuntimeException("could not find db conf to start with");
			}
			return new StaticMysqlHostSelector(confOrNull);
		}
		
	}
}
