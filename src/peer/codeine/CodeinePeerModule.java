package codeine;

import codeine.collectors.CollectorsListHolderFactory;
import codeine.collectors.CollectorsRunnerFactory;
import codeine.collectors.OneCollectorRunnerFactory;
import codeine.collectors.tags.TagsCollectorRunnerFactory;
import codeine.collectors.version.VersionCollectorRunnerFactory;
import codeine.configuration.IConfigurationManager;
import codeine.db.IAlertsDatabaseConnector;
import codeine.db.IStatusDatabaseConnector;
import codeine.db.ProjectsConfigurationConnector;
import codeine.db.mysql.DBConnection;
import codeine.db.mysql.IDBConnection;
import codeine.db.mysql.MysqlHostSelector;
import codeine.db.mysql.NearestMysqlHostSelectorPeer;
import codeine.db.mysql.connectors.NotificationsMysqlConnector;
import codeine.db.mysql.connectors.ProjectsConfigurationMysqlConnector;
import codeine.db.mysql.connectors.StatusMysqlConnector;
import codeine.jsons.nodes.NodesManager;
import codeine.jsons.peer_status.PeerStatus;
import codeine.nodes.NodesManagerPeer;
import codeine.nodes.NodesRunner;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class CodeinePeerModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IAlertsDatabaseConnector.class).to(NotificationsMysqlConnector.class);
		bind(ProjectsConfigurationConnector.class).to(ProjectsConfigurationMysqlConnector.class);
		bind(IStatusDatabaseConnector.class).to(StatusMysqlConnector.class);
		bind(MysqlHostSelector.class).to(NearestMysqlHostSelectorPeer.class).in(Scopes.SINGLETON);
		bind(IDBConnection.class).to(DBConnection.class).in(Scopes.SINGLETON);
		bind(PeerStatus.class).in(Scopes.SINGLETON);
		bind(IConfigurationManager.class).to(ConfigurationManagerPeer.class).in(Scopes.SINGLETON);
		bind(NodesRunner.class).in(Scopes.SINGLETON);
		bind(ConfigurationGetter.class).in(Scopes.SINGLETON);
		bind(PeerStatusChangedUpdater.class).in(Scopes.SINGLETON);
		bind(NodesManager.class).to(NodesManagerPeer.class).in(Scopes.SINGLETON);
		bind(SnoozeKeeper.class).in(Scopes.SINGLETON);
		install(new FactoryModuleBuilder().build(CollectorsRunnerFactory.class));
		install(new FactoryModuleBuilder().build(CollectorsListHolderFactory.class));
		install(new FactoryModuleBuilder().build(OneCollectorRunnerFactory.class));
		install(new FactoryModuleBuilder().build(VersionCollectorRunnerFactory.class));
		install(new FactoryModuleBuilder().build(TagsCollectorRunnerFactory.class));
	}

}
