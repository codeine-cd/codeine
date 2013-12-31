package codeine.servlets.api_servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.model.Constants;
import codeine.servlet.AbstractServlet;
import codeine.utils.os_process.ProcessExecuter;

public class UpgradeApiServlet extends AbstractServlet {
	
	private static final Logger log = Logger.getLogger(UpgradeApiServlet.class);
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		String cmd = Constants.getInstallDir() + "/bin/upgrade.pl";
		log.info("going to upgrade " + cmd);
		ProcessExecuter.execute(cmd);
	}
	
}
