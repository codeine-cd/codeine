package yami;

import javax.inject.Inject;

import org.eclipse.jetty.server.Server;

import yami.configuration.ConfigurationManager;
import yami.model.DataStore;
import yami.model.IDataStore;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Scopes;

public class YamiPeerModule extends AbstractModule
{
	

	@Override
	protected void configure()
	{
		bind(ConfigurationManager.class).in(Scopes.SINGLETON);
		bind(IDataStore.class).to(DataStore.class).in(Scopes.SINGLETON);
		bind(Server.class).toProvider(new ServerProvider()).in(Scopes.SINGLETON);
	}
	
	public static class ServerProvider implements Provider<Server> {
		@Inject ConfigurationManager cm;
		@Override
		public Server get() {
			int port = cm.getCurrentGlobalConfiguration().getPeerPort();
			return new Server(port);
		}
	}

}
