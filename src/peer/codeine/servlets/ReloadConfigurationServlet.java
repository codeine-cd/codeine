package codeine.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.configuration.IConfigurationManager;
import codeine.nodes.NodesRunner;

import com.google.inject.Inject;

public class ReloadConfigurationServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(ReloadConfigurationServlet.class);
	private static final long serialVersionUID = 1L;
	@Inject
	private NodesRunner nodesRunner;
	@Inject
	private IConfigurationManager configurationManager;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		log.info("ReloadConfigurationServlet started");
		configurationManager.refresh();
		nodesRunner.run();
		res.getWriter().print("{result:'OK'}");
	}
}
