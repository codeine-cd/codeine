package codeine.servlets.api_servlets.angular;


import codeine.jsons.global.ExperimentalConfJson;
import codeine.jsons.global.ExperimentalConfJsonStore;
import codeine.plugins.CodeineConfModifyPlugin;
import codeine.plugins.CodeineConfModifyPlugin.Step;
import codeine.servlet.AbstractApiServlet;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

public class CodeineExperimentalConfigurationApiServlet extends AbstractApiServlet {

	
	private static final Logger log = Logger.getLogger(CodeineExperimentalConfigurationApiServlet.class);

	private static final long serialVersionUID = 1L;

	@Inject private ExperimentalConfJsonStore configurationJsonStore;
	@Inject private CodeineConfModifyPlugin codeineConfModifyPlugin;
	
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
		log.info("Will update codeine Experimental configuration. New Config is: " + config);
		codeineConfModifyPlugin.call(Step.pre, getUser(req).user().username());
		configurationJsonStore.store(config);
		codeineConfModifyPlugin.call(Step.post, getUser(req).user().username());
		writeResponseJson(resp, configurationJsonStore.get());
	}

}
