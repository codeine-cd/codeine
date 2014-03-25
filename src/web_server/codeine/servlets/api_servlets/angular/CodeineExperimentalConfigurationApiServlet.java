package codeine.servlets.api_servlets.angular;


import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.jsons.global.ExperimentalConfJson;
import codeine.jsons.global.ExperimentalConfJsonStore;
import codeine.servlet.AbstractServlet;

public class CodeineExperimentalConfigurationApiServlet extends AbstractServlet {

	
	private static final Logger log = Logger.getLogger(CodeineExperimentalConfigurationApiServlet.class);

	private static final long serialVersionUID = 1L;

	@Inject private ExperimentalConfJsonStore configurationJsonStore;
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		if (request.getMethod().equals("PUT")) {
			if (!isAdministrator(request)) {
				log.info("User can not update configuration");
				return false;
			}
			return true;
		}
		return true;
	}
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		writeResponseJson(response, configurationJsonStore.get());
	}
	
	@Override
	protected void myPut(HttpServletRequest req, HttpServletResponse resp) {
		ExperimentalConfJson config = readBodyJson(req, ExperimentalConfJson.class);
		log.info("Will update codeine Experimenta configuration. New Config is: " + config);
		configurationJsonStore.store(config);
		writeResponseJson(resp, configurationJsonStore.get());
	}

}
