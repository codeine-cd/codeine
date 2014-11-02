package codeine.servlets.api_servlets.angular;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeine.configuration.PathHelper;
import codeine.jsons.CommandExecutionStatusInfo;
import codeine.model.Constants;
import codeine.servlet.AbstractApiServlet;
import codeine.utils.JsonFileUtils;
import codeine.utils.TextFileUtils;

public class CommandStatusApiServlet extends AbstractApiServlet {

	private static final long serialVersionUID = 1L;
	@Inject private PathHelper pathHelper;
	
	protected CommandStatusApiServlet() {
	}

	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		String projectName = getParameter(request, Constants.UrlParameters.PROJECT_NAME);
		String commandName = getParameter(request, Constants.UrlParameters.COMMAND_NAME);
		String file = pathHelper.getCommandOutputInfoFile(projectName, commandName);
		String outputfile = pathHelper.getCommandOutputFile(projectName, commandName);
		CommandExecutionStatusInfo commandInfo = new JsonFileUtils(gson()).getConfFromFile(file, CommandExecutionStatusInfo.class);
		commandInfo.output(TextFileUtils.getContents(outputfile));
		writeResponseGzipJson(commandInfo, request, response);
	}
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return canReadProject(request);
	}

}
