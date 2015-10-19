package codeine.servlets;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeine.executer.ThreadPoolUtils;
import org.apache.log4j.Logger;

import codeine.configuration.IConfigurationManager;
import codeine.nodes.NodesRunner;

import com.google.inject.Inject;
import sun.nio.ch.ThreadPool;

public class ReloadConfigurationServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(ReloadConfigurationServlet.class);
	private static final long serialVersionUID = 1L;
	@Inject
	private NodesRunner nodesRunner;
	@Inject
	private IConfigurationManager configurationManager;
	private ExecutorService threadPool = ThreadPoolUtils.newThreadPool(1, "ReloadConfigurationServlet");

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		log.info("ReloadConfigurationServlet called");
		Runnable thread = new Runnable() {
			@Override
			public void run() {
				try {
					int millis = new Random().nextInt((int) TimeUnit.MINUTES.toMillis(1));
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
		threadPool.submit(thread);
		res.getWriter().print("{result:'OK'}");
	}
}
