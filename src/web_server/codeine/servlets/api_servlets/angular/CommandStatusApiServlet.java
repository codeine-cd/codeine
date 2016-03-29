package codeine.servlets.api_servlets.angular;

import codeine.api.CommandExecutionStatusInfo;
import codeine.api.NodeInfoNameAndAlias;
import codeine.command_peer.AllNodesCommandExecuter;
import codeine.command_peer.NodesCommandExecuterProvider;
import codeine.configuration.PathHelper;
import codeine.model.Constants;
import codeine.permissions.IUserWithPermissions;
import codeine.servlet.AbstractApiServlet;
import codeine.utils.JsonFileUtils;
import codeine.utils.TextFileUtils;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class is used for command status page
 */
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
		String includeOutputParameter = getParameter(request, Constants.UrlParameters.INCLUDE_OUTPUT);
		boolean includeOutput = OptionalParameter.getValue(includeOutputParameter);
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
		if (includeOutput) {
			commandInfo.output(TextFileUtils.getContents(outputfile));
		}
		commandInfo.clearPasswordParams();
		commandInfo.can_cancel(getUser(request).isAdministrator() || getUser(request).user().username().equals(commandInfo.user()));
		commandInfo.can_rerun(canRerun(request, projectName, commandInfo));
		writeResponseGzipJson(commandInfo, request, response);
	}

	private boolean canRerun(HttpServletRequest request, String projectName, CommandExecutionStatusInfo commandInfo) {
		boolean canRerun = true;
		IUserWithPermissions user = getUser(request);
		for (NodeInfoNameAndAlias node : commandInfo.nodes_list()) {
			if (!user.canCommand(projectName, node.alias())) {
				canRerun = false;
				break;
			}
		}
		return canRerun;
	}

	private CommandExecutionStatusInfo getCommandInfo(String file) {
		return new JsonFileUtils(gson()).getConfFromFile(file, CommandExecutionStatusInfo.class);
	}
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return canReadProject(request);
	}

}
