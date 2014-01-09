package codeine;

import java.util.List;

import org.apache.log4j.Logger;

import codeine.db.mysql.MysqlDatabaseSchemaManagement;
import codeine.db.mysql.MysqlHostSelector;
import codeine.db.mysql.MysqlProcessControlService;
import codeine.executer.PeriodicExecuter;
import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.jsons.global.MysqlConfigurationJson;
import codeine.jsons.peer_status.PeersProjectsStatus;
import codeine.peers_status.OldPeersRemove;
import codeine.servlets.CodeineDirectoryServletModule;

import com.google.common.collect.Lists;
import com.google.inject.Injector;
import com.google.inject.Module;

public class CodeineDirectoryBootstrap extends AbstractCodeineBootstrap
{
	
	private static final Logger log = Logger.getLogger(CodeineDirectoryBootstrap.class);

	public CodeineDirectoryBootstrap(Injector injector) {
		super(injector);
	}

	public CodeineDirectoryBootstrap() {
	}

	public static void main(String[] args)
	{
		boot(Component.DIRECTORY, CodeineDirectoryBootstrap.class);
	}

	@Override
	protected List<Module> getGuiceModules() {
		return Lists.<Module>newArrayList(new CodeineDirectoryModule(), new CodeineDirectoryServletModule());
	}

	@Override
	protected void execute() throws Exception {
		log.info("starting mysql");
		MysqlConfigurationJson conf = injector().getInstance(MysqlHostSelector.class).getLocalConf();
		if (conf.managed_by_codeine()) {
			injector().getInstance(MysqlProcessControlService.class).execute();
		}
		injector().getInstance(MysqlDatabaseSchemaManagement.class).initDatabase();
		new PeriodicExecuter(OldPeersRemove.INTERVAL ,injector().getInstance(OldPeersRemove.class)).runInThread();
		new PeriodicExecuter(PeersProjectsStatus.SLEEP_TIME ,injector().getInstance(PeersProjectsStatus.class)).runInThread();
	}

	@Override
	public int getHttpPort() {
		return injector().getInstance(GlobalConfigurationJsonStore.class).get().directory_port();
	}
	
}
