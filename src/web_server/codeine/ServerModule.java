package codeine;


import codeine.command_peer.NodesCommandExecuterProvider;
import codeine.jsons.peer_status.PeersProjectsStatus;
import codeine.users.UsersManager;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class ServerModule extends AbstractModule
{
	
	@Override
	protected void configure()
	{
		bind(PeersProjectsStatus.class).in(Scopes.SINGLETON);
		bind(UsersManager.class).in(Scopes.SINGLETON);
		bind(NodesCommandExecuterProvider.class).in(Scopes.SINGLETON);
	}
	
}
