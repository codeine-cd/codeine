package codeine;

import javax.inject.Inject;

import codeine.db.IAlertsDatabaseConnector;
import codeine.db.IStatusDatabaseConnector;
import codeine.db.ProjectsConfigurationConnector;
import codeine.db.mysql.MysqlHostSelector;
import codeine.db.mysql.connectors.AlertsMysqlConnector;
import codeine.db.mysql.connectors.ProjectsConfigurationMysqlConnector;
import codeine.db.mysql.connectors.StatusMysqlConnector;
import codeine.jsons.auth.IdentityConfJson;
import codeine.jsons.auth.PermissionsConfJson;
import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.jsons.global.UserPermissionsJsonStore;
import codeine.jsons.global.ExperimentalConfJsonStore;
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
//		bind(MongoClient.class).toProvider(MongoClientProvider.class).in(Scopes.SINGLETON);
		bind(IAlertsDatabaseConnector.class).to(AlertsMysqlConnector.class);
		bind(ProjectsConfigurationConnector.class).to(ProjectsConfigurationMysqlConnector.class);
		bind(IStatusDatabaseConnector.class).to(StatusMysqlConnector.class);
		bind(LabelJsonProvider.class).to(LabelJsonFromFileProvider.class).in(Scopes.SINGLETON);
		bind(MysqlHostSelector.class).in(Scopes.SINGLETON);
		//TODO make everybody use store to update immediately
		//bind(GlobalConfigurationJson.class).toProvider(new GlobalConfigurationJsonStore()).in(Scopes.SINGLETON);
		bind(GlobalConfigurationJsonStore.class).in(Scopes.SINGLETON);
		bind(IdentityConfJson.class).toProvider(new IdentityConfJsonProvider()).in(Scopes.SINGLETON);
		bind(PermissionsConfJson.class).toProvider(new UserPermissionsJsonStore()).in(Scopes.SINGLETON);
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
