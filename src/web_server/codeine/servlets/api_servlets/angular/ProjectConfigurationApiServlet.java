package codeine.servlets.api_servlets.angular;


import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.configuration.IConfigurationManager;
import codeine.model.Constants;
import codeine.servlet.AbstractServlet;

public class ProjectConfigurationApiServlet extends AbstractServlet {

	
	private static final Logger log = Logger.getLogger(ProjectConfigurationApiServlet.class);

	private static final long serialVersionUID = 1L;

	@Inject private IConfigurationManager configurationManager;
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return canConfigureProject(request);
	}
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		writeResponseJson(response, configurationManager.getProjectForName(request.getParameter(Constants.UrlParameters.PROJECT_NAME)));
	}
	
//	@Override
//	protected void myPut(HttpServletRequest req, HttpServletResponse resp) {
//		GlobalConfigurationJson config = readBodyJson(req, GlobalConfigurationJson.class);
//		log.info("Will update codeine configuration. New Config is: " + config);
//		configurationJsonStore.store(config);
//		writeResponseJson(resp, configurationJsonStore.get());
//	}

}
