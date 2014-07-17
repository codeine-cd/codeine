package codeine.servlets.api_servlets;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.model.Constants;
import codeine.model.Constants.UrlParameters;
import codeine.model.Result;
import codeine.servlet.AbstractApiServlet;
import codeine.utils.os_process.ProcessExecuter.ProcessExecuterBuilder;

import com.google.common.collect.Lists;

public class UpgradeApiServlet extends AbstractApiServlet {
	
	private static final Logger log = Logger.getLogger(UpgradeApiServlet.class);
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		String version = request.getParameter(UrlParameters.VERSION_NAME);
		if (version.contains(" ") || version.contains(";")) {
			throw new IllegalArgumentException("bad version " + version);
		}
		List<String> cmd = Lists.newArrayList(Constants.getInstallDir() + "/bin/upgrade.pl","--version",version);
		log.info("going to upgrade: " + cmd);
		Result r = new ProcessExecuterBuilder(cmd).build().execute();
		PrintWriter writer = getWriter(response);
		writer.write(r.output);
	}
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return isAdministrator(request);
	}
}
