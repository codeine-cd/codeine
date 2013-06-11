package yami;

import yami.configuration.ConfigurationManager;
import yami.model.DataStore;
import yami.model.IDataStore;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class YamiServerModule extends AbstractModule
{
	
	@Override
	protected void configure()
	{
		bind(ConfigurationManager.class).in(Scopes.SINGLETON);
		bind(IDataStore.class).to(DataStore.class).in(Scopes.SINGLETON);
		bind(SendMailStrategy.class);
		bind(CollectorHttpResultFetcher.class);
	}
	
}
