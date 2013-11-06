package codeine;

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
		bind(VersionsMappingStore.class).in(Scopes.SINGLETON);
		bind(VersionsMapping.class).toProvider(VersionsMappingProvider.class).in(Scopes.SINGLETON);
	}

}
