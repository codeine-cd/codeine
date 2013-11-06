package codeine.servlets;

import java.io.PrintWriter;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.configuration.ConfigurationManager;
import codeine.configuration.PathHelper;
import codeine.credentials.CredentialsHelper;
import codeine.jsons.command.CommandJson;
import codeine.model.Constants;
import codeine.model.Result;
import codeine.servlet.AbstractServlet;
import codeine.utils.FilesUtils;
import codeine.utils.StringUtils;
import codeine.utils.network.InetUtils;
import codeine.utils.os_process.ProcessExecuter.ProcessExecuterBuilder;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class CommandNodeServlet extends AbstractServlet
{
	private static final Logger log = Logger.getLogger(CommandNodeServlet.class);
	private static final long serialVersionUID = 1L;
	@Inject private PathHelper pathHelper;
	@Inject private ConfigurationManager configurationManager;
	
	@Override
	public void myGet(HttpServletRequest req, HttpServletResponse res)
	{
		log.info("CommandNodeServlet doGet");
		String projectName = req.getParameter("project");
		final PrintWriter writer = getWriter(res);
		String command = req.getParameter("command");
		String userArgs = req.getParameter("version");
		writer.println("projectName = " + projectName);
		writer.println("command = " + command);
		writer.println("user_args = " + userArgs);
		writer.println("INFO: CommandNodeServlet - executing command " + command + " on host " + InetUtils.getLocalHost().getHostName() +" in project " + projectName + " with args " + userArgs);
		CommandJson commandJson = getCommand(command, projectName);
		
		String dir = pathHelper.getPluginsDir(projectName);
		String file = dir + "/" + command;
		if (!FilesUtils.exists(file)){
			log.info("command not found " + file);
			writer.println("command not found " + file);
			dir = pathHelper.getMonitorsDir(projectName);
			file = dir + "/" + command;
			if (!FilesUtils.exists(file)){
				log.info("2.command not found " + file);
				writer.println("2.command not found " + file);
				writer.println(Constants.COMMAND_RESULT + "-2");
				return;
			}
			else {
				log.warn("command is in monitors and not in plugins. this is deprecated and will be removed soon (backward competability to version 371) " + command, new RuntimeException());
			}
		}
		List<String> cmd = Lists.newArrayList();
		List<String> cmdForOutput = Lists.newArrayList();
		String credentials = commandJson.credentials();
		if (credentials != null){
			writer.println("credentials = " + credentials);
			cmd.add(PathHelper.getReadLogs());
			cmd.add(encodeIfNeeded(credentials, credentials));
		}
		cmd.add(encodeIfNeeded(file, credentials));
		cmdForOutput.add(file);
		if (userArgs != null)
		{
			cmd.add(encodeIfNeeded(userArgs, credentials));
			cmdForOutput.add(userArgs);
		}
		writer.println("executing: " + StringUtils.collectionToString(cmdForOutput));
		writer.println("output: ");
		Function<String, Void> function = new Function<String, Void>(){
			@Override
			public Void apply(String input){
				writer.println(input);
				writer.flush();
				return null;
			}
		};
		Result result = new ProcessExecuterBuilder(cmd, dir).cmdForOutput(cmdForOutput).timeoutInMinutes(10).function(function).build().execute();
		writer.println(Constants.COMMAND_RESULT + result.exit());
	}

	private String encodeIfNeeded(String text, String credentials) {
		return null == credentials ? text: new CredentialsHelper().encode(text);
	}

	private CommandJson getCommand(String command, String projectName) {
		return configurationManager.getProjectForName(projectName).getCommand(command);
	}
}
