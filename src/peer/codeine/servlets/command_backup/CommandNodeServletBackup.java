package codeine.servlets.command_backup;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;

import codeine.SnoozeKeeper;
import codeine.configuration.IConfigurationManager;
import codeine.configuration.PathHelper;
import codeine.credentials.CredHelper;
import codeine.jsons.auth.EncryptionUtils;
import codeine.jsons.command.CommandInfo;
import codeine.jsons.command.CommandInfoForSpecificNode;
import codeine.jsons.command.CommandParameterInfo;
import codeine.jsons.global.ExperimentalConfJsonStore;
import codeine.jsons.peer_status.PeerStatus;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.model.ExitStatus;
import codeine.model.Result;
import codeine.servlet.AbstractServlet;
import codeine.servlets.command_backup.ProcessExecuterBackup.ProcessExecuterBuilderBackup;
import codeine.utils.StringUtils;
import codeine.utils.os.OperatingSystem;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class CommandNodeServletBackup extends AbstractServlet
{
	private static final Logger log = Logger.getLogger(CommandNodeServletBackup.class);
	private static final long serialVersionUID = 1L;
	@Inject private PathHelper pathHelper;
	@Inject private IConfigurationManager configurationManager;
	@Inject private ExperimentalConfJsonStore experimentalConfJsonStore;
	@Inject private SnoozeKeeper snoozeKeeper;
	@Inject private PeerStatus projectStatusUpdater;
	
	
	@Override
	public void myPost(HttpServletRequest request, HttpServletResponse res)	{
		log.info("start handle command");
		if (Boolean.parseBoolean(getParameter(request, Constants.UrlParameters.FORCE)) || experimentalConfJsonStore.get().allow_concurrent_commands_in_peer()) {
			executeCommandNotSync(request, res);
		}
		else {
			executeCommandSync(request, res);
		}
		log.info("finished handle command");
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
		ShellScriptBackup cmdScript = null;
		snoozeKeeper.snoozeAll();
		final PrintWriter writer = getWriter(res);
		try {
			String parameter = Constants.UrlParameters.DATA_NAME;
			String data = getParameter(request, parameter);
			CommandInfo commandInfo = gson().fromJson(data, CommandInfo.class);
			String data2 = getParameter(request, Constants.UrlParameters.DATA_ADDITIONAL_COMMAND_INFO_NAME);
			CommandInfoForSpecificNode commandInfo2 = gson().fromJson(data2, CommandInfoForSpecificNode.class);
			if (null != commandInfo2.key()) {
				String decrypt = EncryptionUtils.decrypt(Constants.CODEINE_API_TOKEN_DERIVER, commandInfo2.key());
				validateKey(decrypt);
			}
			else {
				log.warn("key is null", new RuntimeException());
			}
			//		writer.println("INFO: Executing on node " + commandInfo2.node_alias());

			String dir = pathHelper.getCommandsDir(commandInfo.project_name());
			String script_content = commandInfo.script_content();
			String file = dir + File.separator + commandInfo.command_name();
			ProjectJson project = getProject(commandInfo.project_name());
			boolean windows_peer = project.operating_system() == OperatingSystem.Windows;
			if (null != script_content){
				//new
				cmdScript = new ShellScriptBackup(file, script_content, windows_peer, commandInfo2.tmp_dir());
				file = cmdScript.create();
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
			if (!StringUtils.isEmpty(credentials) && !windows_peer){
				writer.println("credentials = " + credentials);
				cmd.add(PathHelper.getReadLogs());
				cmd.add(encodeIfNeeded(credentials, credentials));
			}
			if (windows_peer) {
				cmd.add(encodeIfNeeded("cmd", credentials));
				cmd.add(encodeIfNeeded("/c", credentials));
				cmd.add(encodeIfNeeded("call", credentials));
			}
			else {
				cmd.add(encodeIfNeeded("/bin/sh", credentials));
				cmd.add(encodeIfNeeded("-xe", credentials));
			}
			cmd.add(encodeIfNeeded(file, credentials));
			if (windows_peer) {
				cmdForOutput.add("cmd");
				cmdForOutput.add("/c");
				cmdForOutput.add("call");
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
			Result result = new ProcessExecuterBuilderBackup(cmd, pathHelper.getProjectDir(commandInfo.project_name())).cmdForOutput(cmdForOutput).timeoutInMinutes(commandInfo.timeoutInMinutes()).function(function).env(env).build().execute();
			writer.println(Constants.COMMAND_RESULT + result.exit());
			writer.flush();
			log.info("command exit status is " + result.exit());
		} catch (Exception ex) {
			try {
				log.warn("failed on command execution", ex);
				writer.println(Constants.COMMAND_RESULT + ExitStatus.EXCEPTION);
			} catch (Exception e) {
				log.warn("failed on command execution2", ex);
			}
		}
		finally {
			if (null != cmdScript){
				cmdScript.delete();
			}
		}
	}
	private void validateKey(String decrypt) {
		List<String> l = Splitter.on("#").splitToList(decrypt);
		if (l.size() != 2){
			log.warn("format error");
			return;
		}
		try {
			UUID.fromString(l.get(0));
		} catch (Exception e) {
			log.warn("format error bad parameter(0) " + l.get(0), e);
		}
		try {
			long currentTimeMillis = System.currentTimeMillis();
			long serverTime = Long.valueOf(l.get(1));
			if (Math.abs(currentTimeMillis - serverTime) > TimeUnit.MINUTES.toMillis(1)) {
				log.warn("time from server does not match");
			}
		} catch (NumberFormatException e) {
			log.warn("format error bad parameter(1) " + l.get(1), e);
		}
	}
	private Map<String, String> getEnvParams(CommandInfo commandJson) {
		Map<String, String> $ = Maps.newHashMap();
		for (CommandParameterInfo p : commandJson.parameters()) {
			$.put(p.name(), p.value());
		}
		return $;
	}

	private String encodeIfNeeded(String text, String credentials) {
		return null == credentials ? text: CredHelper.encode(text);
	}

	private ProjectJson getProject(String projectName) {
		return configurationManager.getProjectForName(projectName);
	}
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return true;
	}
}
