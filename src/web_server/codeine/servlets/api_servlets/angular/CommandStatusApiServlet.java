package codeine.servlets.api_servlets.angular;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.command_peer.AllNodesCommandExecuter;
import codeine.command_peer.NodesCommandExecuterProvider;
import codeine.configuration.PathHelper;
import codeine.jsons.CommandExecutionStatusInfo;
import codeine.model.Constants;
import codeine.servlet.AbstractApiServlet;
import codeine.utils.JsonFileUtils;
import codeine.utils.TextFileUtils;

public class CommandStatusApiServlet extends AbstractApiServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(CommandStatusApiServlet.class);
	@Inject private PathHelper pathHelper;
	@Inject private NodesCommandExecuterProvider allNodesCommandExecuterProvider;
	
	protected CommandStatusApiServlet() {
	}

	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		String projectName = getParameter(request, Constants.UrlParameters.PROJECT_NAME);
		String commandName = getParameter(request, Constants.UrlParameters.COMMAND_NAME);
		String file = pathHelper.getCommandOutputInfoFile(projectName, commandName);
		String outputfile = pathHelper.getCommandOutputFile(projectName, commandName);
		AllNodesCommandExecuter e = allNodesCommandExecuterProvider.getCommandOrNull(projectName, commandName);
		CommandExecutionStatusInfo commandInfo;
		if (e == null) {
			commandInfo = getCommandInfo(file);
		} else {
			log.info("command is running so getting a lock " + e.commandString());
			synchronized (e.fileWriteSync()) {
				commandInfo = getCommandInfo(file);
			}
		}
		commandInfo.output(TextFileUtils.getContents(outputfile));
		writeResponseGzipJson(commandInfo, request, response);
	}

	private CommandExecutionStatusInfo getCommandInfo(String file) {
		return new JsonFileUtils(gson()).getConfFromFile(file, CommandExecutionStatusInfo.class);
	}
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return canReadProject(request);
	}

}
