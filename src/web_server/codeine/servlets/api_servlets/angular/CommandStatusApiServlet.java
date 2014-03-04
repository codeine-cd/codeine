package codeine.servlets.api_servlets.angular;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeine.configuration.PathHelper;
import codeine.jsons.CommandExecutionStatusInfo;
import codeine.model.Constants;
import codeine.servlet.AbstractServlet;
import codeine.utils.JsonFileUtils;
import codeine.utils.TextFileUtils;

public class CommandStatusApiServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	@Inject private PathHelper pathHelper;
	
	protected CommandStatusApiServlet() {
	}

	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		String commandName = request.getParameter(Constants.UrlParameters.COMMAND_NAME);
		String file = pathHelper.getCommandOutputInfoFile(projectName, commandName);
		String outputfile = pathHelper.getCommandOutputFile(projectName, commandName);
		CommandExecutionStatusInfo commandInfo = new JsonFileUtils(gson()).getConfFromFile(file, CommandExecutionStatusInfo.class);
		commandInfo.output(TextFileUtils.getContents(outputfile));
		writeResponseGzipJson(response, commandInfo);
	}
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return canReadProject(request);
	}

}
