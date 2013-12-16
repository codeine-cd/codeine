package codeine;

import codeine.configuration.IConfigurationManager;
import codeine.jsons.nodes.NodesManager;
import codeine.jsons.peer_status.PeerStatus;
import codeine.nodes.NodesManagerPeer;
import codeine.nodes.NodesRunner;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class CodeinePeerModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IConfigurationManager.class).to(ConfigurationManagerPeer.class).in(Scopes.SINGLETON);
		bind(PeerStatus.class).in(Scopes.SINGLETON);
		bind(NodesRunner.class).in(Scopes.SINGLETON);
		bind(PeerStatusChangedUpdater.class).in(Scopes.SINGLETON);
		bind(NodesManager.class).to(NodesManagerPeer.class).in(Scopes.SINGLETON);
		bind(SnoozeKeeper.class).in(Scopes.SINGLETON);
	}

}
