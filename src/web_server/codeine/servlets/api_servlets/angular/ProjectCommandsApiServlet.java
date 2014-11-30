package codeine.servlets.api_servlets.angular;


import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.ConfigurationManagerServer;
import codeine.jsons.command.CommandInfo;
import codeine.model.Constants;
import codeine.servlet.AbstractApiServlet;
import codeine.utils.network.RequestUtils;

public class ProjectCommandsApiServlet extends AbstractApiServlet {

	
	private static final Logger log = Logger.getLogger(ProjectCommandsApiServlet.class);
	private static final long serialVersionUID = 1L;

	@Inject private ConfigurationManagerServer configurationManager;
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return canReadProject(request);
	}
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		log.info("ProjectCommandsApiServlet get");
		String projectName = RequestUtils.getParameter(request, Constants.UrlParameters.PROJECT_NAME);
		List<CommandInfo> commands = configurationManager.getProjectCommands(projectName);
		writeResponseJson(response, commands);
	}
}
