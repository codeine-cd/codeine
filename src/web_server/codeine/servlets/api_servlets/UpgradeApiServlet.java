package codeine.servlets.api_servlets;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.model.Constants;
import codeine.model.Constants.UrlParameters;
import codeine.model.Result;
import codeine.servlet.AbstractServlet;
import codeine.utils.os_process.ProcessExecuter;

public class UpgradeApiServlet extends AbstractServlet {
	
	private static final Logger log = Logger.getLogger(UpgradeApiServlet.class);
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		String version = request.getParameter(UrlParameters.VERSION_NAME);
		String cmd = Constants.getInstallDir() + "/bin/upgrade.pl --version " + version;
		log.info("going to upgrade: " + cmd);
		Result r = ProcessExecuter.execute(cmd);
		PrintWriter writer = getWriter(response);
		writer.write(r.output);
	}
	
}
