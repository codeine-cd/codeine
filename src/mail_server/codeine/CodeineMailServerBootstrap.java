package codeine;

import java.util.List;
import java.util.concurrent.TimeUnit;

import codeine.executer.PeriodicExecuter;
import codeine.jsons.global.GlobalConfigurationJsonStore;

import com.google.common.collect.Lists;
import com.google.inject.Injector;
import com.google.inject.Module;

public class CodeineMailServerBootstrap extends AbstractCodeineBootstrap
{
	public CodeineMailServerBootstrap(Injector injector) {
		super(injector);
	}

	public CodeineMailServerBootstrap() {
	}

	public static void main(String[] args)
	{
		boot(Component.MAIL, CodeineMailServerBootstrap.class);
	}
	
	@Override
	protected void execute() throws Exception
	{
		new PeriodicExecuter(TimeUnit.SECONDS.toMillis(1), injector().getInstance(MonitorDBTask.class)).runInThread();
	}

	@Override
	protected List<Module> getGuiceModules() {
		List<Module> $ = Lists.newArrayList();
		$.add(new MailServerModule());
		return $;
	}

	@Override
	public int getHttpPort() {
		return injector().getInstance(GlobalConfigurationJsonStore.class).get().mail_server_port();
	}
	
}
