package codeine;

import java.util.List;

import org.apache.log4j.Logger;

import codeine.db.mysql.MysqlProcessControlService;
import codeine.executer.PeriodicExecuter;
import codeine.jsons.global.GlobalConfigurationJson;
import codeine.peers_status.OldPeersRemove;
import codeine.servlets.CodeineDirectoryServletModule;

import com.google.common.collect.Lists;
import com.google.inject.Module;

public class CodeineDirectoryBootstrap extends AbstractCodeineBootstrap
{
	
	private static final Logger log = Logger.getLogger(CodeineDirectoryBootstrap.class);

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
		injector().getInstance(MysqlProcessControlService.class).execute();
		new Thread(new PeriodicExecuter(OldPeersRemove.INTERVAL ,injector().getInstance(OldPeersRemove.class), "OldPeersRemove")).start();
	}

	@Override
	public int getHttpPort() {
		return injector().getInstance(GlobalConfigurationJson.class).directory_port();
	}
	
}
