package codeine.servlets;

import codeine.configuration.IConfigurationManager;
import codeine.executer.ThreadPoolUtils;
import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.model.Constants.UrlParameters;
import codeine.nodes.NodesRunner;
import codeine.servlet.AbstractServlet;
import com.google.inject.Inject;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

public class ReloadConfigurationServlet extends AbstractServlet {
	private static final Logger log = Logger.getLogger(ReloadConfigurationServlet.class);
	private static final long serialVersionUID = 1L;
	@Inject
	private NodesRunner nodesRunner;
	@Inject
	private IConfigurationManager configurationManager;
	@Inject
	private GlobalConfigurationJsonStore globalConfigurationJsonStore;
	private ExecutorService threadPool = ThreadPoolUtils.newThreadPool(1, "ReloadConfigurationServlet");

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		log.info("ReloadConfigurationServlet called");
		String parameter = UrlParameters.SYNC_REQUEST;
		boolean isSync = Boolean.parseBoolean(getParameter(req, parameter));
		Runnable thread = new Runnable() {
			@Override
			public void run() {
				try {
					long duration = globalConfigurationJsonStore.get().large_deployment() ? 60 : 25;
					int millis = new Random().nextInt((int) TimeUnit.SECONDS.toMillis(duration));
					log.info("ReloadConfigurationServlet going to sleep " + millis);
					Thread.sleep(millis);
				} catch (InterruptedException e) {
					log.error(e);
				}
				configurationManager.refresh();
				nodesRunner.run();
				log.info("ReloadConfigurationServlet async finished");
			}
		};
		if (isSync) {
			log.info("Running is sync mode");
			thread.run();
		}
		else {
			threadPool.submit(thread);
		}
		res.getWriter().print("{result:'OK'}");
	}

	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return true;
	}
}
