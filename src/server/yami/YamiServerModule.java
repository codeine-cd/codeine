package yami;

import yami.configuration.ConfigurationManager;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class YamiServerModule extends AbstractModule
{
	
	@Override
	protected void configure()
	{
		bind(ConfigurationManager.class).in(Scopes.SINGLETON);
		bind(SendMailStrategy.class);
		bind(CollectorHttpResultFetcher.class);
	}
	
}
