package codeine;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

import codeine.configuration.Links;
import codeine.configuration.PathHelper;
import codeine.db.mysql.MysqlHostSelector;
import codeine.db.mysql.NearestMysqlHostSelectorPeer;
import codeine.executer.PeriodicExecuter;
import codeine.executer.Task;
import codeine.jsons.info.CodeineRuntimeInfo;
import codeine.model.Constants;
import codeine.servlets.CodeinePeerServletModule;
import codeine.utils.FilesUtils;
import codeine.utils.TextFileUtils;
import codeine.utils.ThreadUtils;
import codeine.utils.network.InetUtils;

import com.google.common.collect.Lists;
import com.google.inject.Module;

public class CodeinePeerBootstrap extends AbstractCodeineBootstrap
{
	
	private final Logger log = Logger.getLogger(CodeinePeerBootstrap.class);
	private String hostname = InetUtils.getLocalHost().getHostName();	
	
	public static void main(String[] args)
	{
		boot(Component.PEER, CodeinePeerBootstrap.class);
	}

	@Override
	protected void execute() throws Exception
	{
		CodeineRuntimeInfo peerRuntimeInfo = injector().getInstance(CodeineRuntimeInfo.class);
		PathHelper pathHelper = injector().getInstance(PathHelper.class);
		startMysqlSelectorThread();
		log.info("creating local workarea: " + Constants.getHostWorkareaDir());
		FilesUtils.mkdirs(Constants.getHostWorkareaDir());
		log.info("writing pid " + peerRuntimeInfo.pid() + " to file " + pathHelper.getPidFile());
		TextFileUtils.setContents(pathHelper.getPidFile(), String.valueOf(peerRuntimeInfo.pid()));
		int port = peerRuntimeInfo.port();
		log.info("writing port " + port + " to file " + pathHelper.getPortFile());
		TextFileUtils.setContents(pathHelper.getPortFile(), String.valueOf(port));
		log.info("Hostname " + hostname);
		injector().getInstance(SnoozeKeeper.class).snoozeAll();
		new PeriodicExecuter(ConfigurationGetter.INTERVAL, injector().getInstance(ConfigurationGetter.class)).runInThread();
		log.info("starting PeerStatusChangedUpdater");
		ThreadUtils.createThread(injector().getInstance(PeerStatusChangedUpdater.class)).start();
	}

	private void startMysqlSelectorThread() {
		new PeriodicExecuter(NearestMysqlHostSelectorPeer.INTERVAL, (Task) injector().getInstance(MysqlHostSelector.class)).runInThread();
	}
	
	@Override
	protected void specificCreateFileServer(ContextHandlerCollection contexts)
	{
		PathHelper pathHelper = injector().getInstance(PathHelper.class);
		Links links = injector().getInstance(Links.class);
		addHandler(links.getNodeMonitorOutputContextPathAllProjects(), pathHelper.getMonitorOutputDirAllProjects(), contexts);
	}

	@Override
	protected List<Module> getGuiceModules() {
		return Lists.<Module>newArrayList(new CodeinePeerModule(), new CodeinePeerServletModule());
	}

	@Override
	public int getHttpPort() {
		String portString = System.getProperty("codeinePeerPort");
		int port = null == portString ? 0 : Integer.valueOf(portString);
		return port;
	}
	
	@Override
	protected int startServer(ContextHandlerCollection contexts) throws Exception {
		if (0 != getHttpPort()) {
			return super.startServer(contexts);
		}
		Server server = new Server(Constants.DEFAULT_PEER_PORT);
		try {
			return startServer(contexts, server);
		} catch (Exception e) {
			log.warn("could not bind to default port " + Constants.DEFAULT_PEER_PORT + " will fallback to random port", e);
			server.stop();
		}
		server = new Server(0);
		return startServer(contexts, server);
	}
	
}
