package codeine;

import codeine.jsons.auth.IdentityConfJsonStore;
import codeine.jsons.global.ExperimentalConfJsonStore;
import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.jsons.global.UserPermissionsJsonStore;
import codeine.jsons.labels.LabelJsonFromFileProvider;
import codeine.jsons.labels.LabelJsonProvider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class CodeineGeneralModule extends AbstractModule
{
	


	@Override
	protected void configure()
	{
		bind(Gson.class).toInstance(new GsonBuilder().setPrettyPrinting().create());
		bind(LabelJsonProvider.class).to(LabelJsonFromFileProvider.class).in(Scopes.SINGLETON);
		bind(GlobalConfigurationJsonStore.class).in(Scopes.SINGLETON);
		bind(IdentityConfJsonStore.class).in(Scopes.SINGLETON);
		bind(UserPermissionsJsonStore.class).in(Scopes.SINGLETON);
		bind(ExperimentalConfJsonStore.class).in(Scopes.SINGLETON);
	}

}
