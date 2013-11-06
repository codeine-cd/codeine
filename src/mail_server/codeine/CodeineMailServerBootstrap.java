package codeine;

import java.util.List;
import java.util.concurrent.TimeUnit;

import codeine.executer.PeriodicExecuter;
import codeine.jsons.global.GlobalConfigurationJson;

import com.google.common.collect.Lists;
import com.google.inject.Module;

public class CodeineMailServerBootstrap extends AbstractCodeineBootstrap
{
	public static void main(String[] args)
	{
		boot(Component.MAIL, CodeineMailServerBootstrap.class);
	}
	
	@Override
	protected void execute() throws Exception
	{
		new PeriodicExecuter(TimeUnit.SECONDS.toMillis(1), injector().getInstance(MonitorDBTask.class), "MonitorDBTask").run();
	}

	@Override
	protected List<Module> getGuiceModules() {
		List<Module> $ = Lists.newArrayList();
		$.add(new MailServerModule());
		return $;
	}

	@Override
	public int getHttpPort() {
		return injector().getInstance(GlobalConfigurationJson.class).mail_server_port();
	}
	
}
