package codeine;


import codeine.command_peer.NodesCommandExecuterProvider;
import codeine.configuration.IConfigurationManager;
import codeine.jsons.peer_status.PeersProjectsStatus;
import codeine.servlet.UsersManager;
import codeine.statistics.MonitorsStatistics;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class WebServerModule extends AbstractModule
{
	
	@Override
	protected void configure()
	{
		bind(PeersProjectsStatus.class).in(Scopes.SINGLETON);
		bind(ConfigurationManagerServer.class).in(Scopes.SINGLETON);
		bind(IConfigurationManager.class).to(ConfigurationManagerServer.class);
		bind(ProjectConfigurationInPeerUpdater.class).in(Scopes.SINGLETON);
		bind(UsersManager.class).in(Scopes.SINGLETON);
		bind(NodesCommandExecuterProvider.class).in(Scopes.SINGLETON);
		bind(MonitorsStatistics.class).in(Scopes.SINGLETON);
	}
	
}
