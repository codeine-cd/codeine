package codeine.servlets.api_servlets.angular;


import codeine.jsons.global.GlobalConfigurationJson;
import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.plugins.CodeineConfModifyPlugin;
import codeine.plugins.CodeineConfModifyPlugin.Step;
import codeine.servlet.AbstractApiServlet;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

public class CodeineConfigurationApiServlet extends AbstractApiServlet {

	
	private static final Logger log = Logger.getLogger(CodeineConfigurationApiServlet.class);

	private static final long serialVersionUID = 1L;

	@Inject private GlobalConfigurationJsonStore configurationJsonStore;
	@Inject private CodeineConfModifyPlugin codeineConfModifyPlugin;
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		if (request.getMethod().equals("PUT")) {
			if (!isAdministrator(request)) {
				log.info("User can not update configuration " + getUser(request));
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
		GlobalConfigurationJson config = readBodyJson(req, GlobalConfigurationJson.class);
		log.info("Will update codeine configuration. New Config is: " + config);
		codeineConfModifyPlugin.call(Step.pre, getUser(req).user().username());
		configurationJsonStore.store(config);
		codeineConfModifyPlugin.call(Step.post, getUser(req).user().username());
		writeResponseJson(resp, configurationJsonStore.get());
	}

}
