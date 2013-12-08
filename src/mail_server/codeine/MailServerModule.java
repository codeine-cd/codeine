package codeine;

import codeine.configuration.ConfigurationReadManagerServer;
import codeine.configuration.IConfigurationManager;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class MailServerModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IConfigurationManager.class).to(ConfigurationReadManagerServer.class).in(Scopes.SINGLETON);
	}

}
