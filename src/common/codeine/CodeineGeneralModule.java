package codeine;

import javax.inject.Inject;

import codeine.jsons.auth.IdentityConfJson;
import codeine.jsons.global.ExperimentalConfJsonStore;
import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.jsons.global.UserPermissionsJsonStore;
import codeine.jsons.labels.LabelJsonFromFileProvider;
import codeine.jsons.labels.LabelJsonProvider;
import codeine.model.Constants;
import codeine.utils.JsonFileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Scopes;

public class CodeineGeneralModule extends AbstractModule
{
	


	@Override
	protected void configure()
	{
		bind(Gson.class).toInstance(new GsonBuilder().setPrettyPrinting().create());
		bind(LabelJsonProvider.class).to(LabelJsonFromFileProvider.class).in(Scopes.SINGLETON);
		bind(GlobalConfigurationJsonStore.class).in(Scopes.SINGLETON);
		bind(IdentityConfJson.class).toProvider(new IdentityConfJsonProvider()).in(Scopes.SINGLETON);
		bind(UserPermissionsJsonStore.class).in(Scopes.SINGLETON);
		bind(ExperimentalConfJsonStore.class).in(Scopes.SINGLETON);
	}

	//TODO move to JsonStore
	private static final class IdentityConfJsonProvider implements Provider<IdentityConfJson> {
		private @Inject JsonFileUtils jsonFileUtils;
		@Override
		public IdentityConfJson get() {
			return jsonFileUtils.getConfFromFile(Constants.getIdentityConfPath(), IdentityConfJson.class);
		}
	}

}
