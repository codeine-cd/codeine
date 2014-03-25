package codeine.servlets;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;

import codeine.SnoozeKeeper;
import codeine.configuration.IConfigurationManager;
import codeine.configuration.PathHelper;
import codeine.credentials.CredentialsHelper;
import codeine.jsons.command.CommandInfo;
import codeine.jsons.command.CommandInfoForSpecificNode;
import codeine.jsons.command.CommandParameterInfo;
import codeine.jsons.global.ExperimentalConfJsonStore;
import codeine.jsons.peer_status.PeerStatus;
import codeine.model.Constants;
import codeine.model.Result;
import codeine.servlet.AbstractServlet;
import codeine.utils.FilesUtils;
import codeine.utils.StringUtils;
import codeine.utils.network.InetUtils;
import codeine.utils.os_process.ProcessExecuter.ProcessExecuterBuilder;
import codeine.utils.os_process.ShellScript;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class CommandNodeServlet extends AbstractServlet
{
	private static final Logger log = Logger.getLogger(CommandNodeServlet.class);
	private static final long serialVersionUID = 1L;
	@Inject private PathHelper pathHelper;
	@Inject private IConfigurationManager configurationManager;
	@Inject private ExperimentalConfJsonStore experimentalConfJsonStore;
	@Inject private SnoozeKeeper snoozeKeeper;
	@Inject private PeerStatus projectStatusUpdater;
	
	@Override
	public void myPost(HttpServletRequest request, HttpServletResponse res)	{
		log.info("myPost");
		if (Boolean.parseBoolean(request.getParameter(Constants.UrlParameters.FORCE)) || experimentalConfJsonStore.get().allow_concurrent_commands_in_peer()) {
			executeCommandNotSync(request, res);
		}
		else {
			executeCommandSync(request, res);
		}
	}
	/**
	 * this prevents multiple commands on the same peer, so preventing upgrade the peer during command for example
	 */
	private void executeCommandNotSync(HttpServletRequest request, HttpServletResponse res) {
		executeInternal(request, res);
	}
	private synchronized void executeCommandSync(HttpServletRequest request, HttpServletResponse res) {
		executeInternal(request, res);
	}
	private void executeInternal(HttpServletRequest request, HttpServletResponse res) {
		snoozeKeeper.snoozeAll();
		final PrintWriter writer = getWriter(res);
		String data = request.getParameter(Constants.UrlParameters.DATA_NAME);
		CommandInfo commandInfo = gson().fromJson(data, CommandInfo.class);
		String data2 = request.getParameter(Constants.UrlParameters.DATA_ADDITIONAL_COMMAND_INFO_NAME);
		CommandInfoForSpecificNode commandInfo2 = gson().fromJson(data2, CommandInfoForSpecificNode.class);
//		writer.println("INFO: Executing on node " + commandInfo2.node_alias());
		
		String dir = pathHelper.getPluginsDir(commandInfo.project_name());
		String script_content = commandInfo.script_content();
		String file = dir + "/" + commandInfo.command_name();
		ShellScript cmdScript = null;
		try {
			if (null != script_content){
				//new
				cmdScript = new ShellScript(file, script_content, experimentalConfJsonStore.get().windows_peer());
				file = cmdScript.create();
			}
			else if (FilesUtils.exists(file)) { //TODO remove after build 1100
				log.warn("command is in old file format: " + file);
			}
			else {
				log.info("command not found " + file);
				writer.println("command not found " + file);
				res.setStatus(HttpStatus.NOT_FOUND_404);
				return;
			}
			List<String> cmd = Lists.newArrayList();
			List<String> cmdForOutput = Lists.newArrayList();
			String credentials = commandInfo.credentials();
			log.info("credentials: " + credentials);
			if (credentials != null){
				writer.println("credentials = " + credentials);
				cmd.add(PathHelper.getReadLogs());
				cmd.add(encodeIfNeeded(credentials, credentials));
			}
			if (experimentalConfJsonStore.get().windows_peer()) {
				//cmd.add(encodeIfNeeded("cmd", credentials));
				//cmd.add(encodeIfNeeded("/c", credentials));
				//cmd.add(encodeIfNeeded("start", credentials));
			}
			else {
				cmd.add(encodeIfNeeded("/bin/sh", credentials));
				cmd.add(encodeIfNeeded("-xe", credentials));
			}
			cmd.add(encodeIfNeeded(file, credentials));
			if (experimentalConfJsonStore.get().windows_peer()) {
				//cmdForOutput.add("cmd");
				//cmdForOutput.add("/c");
				//cmdForOutput.add("start");
			}
			else {
				cmdForOutput.add("/bin/sh");
				cmdForOutput.add("-xe");
			}
			cmdForOutput.add(file);
			writer.println("$ " + StringUtils.collectionToString(cmdForOutput));
			Function<String, Void> function = new Function<String, Void>(){
				@Override
				public Void apply(String input){
					writer.println(input);
					writer.flush();
					return null;
				}
			};
			Map<String, String> env = getEnvParams(commandInfo);
			env.put(Constants.EXECUTION_ENV_PROJECT_NAME, commandInfo.project_name());
			env.put(Constants.EXECUTION_ENV_NODE_NAME, commandInfo2.node_name());
			env.put(Constants.EXECUTION_ENV_NODE_ALIAS, commandInfo2.node_alias());
			env.put(Constants.EXECUTION_ENV_NODE_TAGS, StringUtils.collectionToString(projectStatusUpdater.getTags(commandInfo.project_name(), commandInfo2.node_name()), ";"));
			Result result = new ProcessExecuterBuilder(cmd, pathHelper.getProjectDir(commandInfo.project_name())).cmdForOutput(cmdForOutput).timeoutInMinutes(10).function(function).env(env).build().execute();
			writer.println(Constants.COMMAND_RESULT + result.exit());
//		res.setStatus(result.success() ? HttpStatus.OK_200 : HttpStatus.INTERNAL_SERVER_ERROR_500);
		} finally {
			if (null != cmdScript){
				cmdScript.delete();
			}
		}
	}
	private Map<String, String> getEnvParams(CommandInfo commandJson) {
		Map<String, String> $ = Maps.newHashMap();
		for (CommandParameterInfo p : commandJson.parameters()) {
			$.put(p.name(), p.value());
		}
		return $;
	}
	private String parameters(CommandInfo commandInfo) {
		Function<CommandParameterInfo, String> function = new Function<CommandParameterInfo, String>() {
			@Override
			public String apply(CommandParameterInfo input) {
				return input.name() + "=" + input.value();
			}
		};
		return StringUtils.collectionToString(commandInfo.parameters(), function);
	}
	//TODO should be removed after build 1000
	@Override
	public void myGet(HttpServletRequest req, HttpServletResponse res)
	{
		log.info("CommandNodeServlet doGet");
		String projectName = req.getParameter("project");
		final PrintWriter writer = getWriter(res);
		writer.println("CommandNodeServlet THE OLD DOGET!");
		String command = req.getParameter("command");
		String userArgs = req.getParameter("version");
		writer.println("projectName = " + projectName);
		writer.println("command = " + command);
		writer.println("user_args = " + userArgs);
		writer.println("INFO: CommandNodeServlet - executing command " + command + " on host " + InetUtils.getLocalHost().getHostName() +" in project " + projectName + " with args " + userArgs);
		CommandInfo commandJson = getCommand(command, projectName);
		
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

	private CommandInfo getCommand(String command, String projectName) {
		return configurationManager.getProjectForName(projectName).getCommand(command);
	}
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return true;
	}
}
