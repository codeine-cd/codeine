package codeine.command_peer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import codeine.api.NodeWithPeerInfo;
import codeine.configuration.Links;
import codeine.jsons.command.CommandInfo;
import codeine.jsons.command.CommandInfoForSpecificNode;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.model.Constants.UrlParameters;
import codeine.model.ExitStatus;
import codeine.permissions.IUserWithPermissions;
import codeine.utils.ExceptionUtils;
import codeine.utils.ThreadUtils;
import codeine.utils.logging.LogUtils;
import codeine.utils.network.HttpUtils;

import com.google.common.base.Function;
import com.google.gson.Gson;

public class PeerCommandWorker implements Runnable {
	
	private static final Logger log = Logger.getLogger(PeerCommandWorker.class);
	private NodeWithPeerInfo node;
	private AllNodesCommandExecuter allNodesCommandExecuter;
	private boolean shouldOutputImmediatly;
	private CommandInfo command_info;
	private Links links;
	private boolean success = false;
	private boolean POST = true;
	private ProjectJson project;
	private Pattern pattern = Pattern.compile(".*" + Constants.COMMAND_RESULT + "(-?\\d+).*");
	private IUserWithPermissions userObject;
	private boolean failedReported = false;
	
	public PeerCommandWorker(NodeWithPeerInfo node, AllNodesCommandExecuter allNodesCommandExecuter, CommandInfo command_info, boolean shouldOutputImmediatly, Links links, ProjectJson project, IUserWithPermissions userObject) {
		this.node = node;
		this.allNodesCommandExecuter = allNodesCommandExecuter;
		this.command_info = command_info;
		this.shouldOutputImmediatly = shouldOutputImmediatly;
		this.links = links;
		this.project = project;
		this.userObject = userObject;
	}


	@Override
	public void run() {
		try {
			execute();
		} finally {
		}
	}

	private long getSleepTime() {
		return 100;
	}

	private void execute() {
		if (noPermissions()) {
			announce("no permissions for user " + userObject.user() + " on node " + node.alias());
		}
		else {
			executeInternal();
		}
		allNodesCommandExecuter.workerFinished();
	}


	private void executeInternal() {
		String url = links.getPeerLink(node.peer_address()) + Constants.COMMAND_NODE_CONTEXT;
		log.info("commandNode url is " + url);
		try {
			ThreadUtils.sleep(getSleepTime());
			log.info("running worker " + node);
			final StringBuilder result = new StringBuilder();
			Function<String, Void> function = new ReadCommandOutputFunction(result);
			if (shouldOutputImmediatly){
				writeNodeHeader();
			}
			if (POST && !command_info.name().equals("upgrade_old_peers")) {
				String key = userObject.user().encodedApiTokenWithTime();
				CommandInfoForSpecificNode command_info2 = new CommandInfoForSpecificNode(node.name(), node.alias(), null, key, project.environmentVariables());
				String postData = UrlParameters.DATA_NAME + "=" + HttpUtils.encodeURL(new Gson().toJson(command_info))
						+"&" + UrlParameters.DATA_ADDITIONAL_COMMAND_INFO_NAME + "=" + HttpUtils.encodeURL(new Gson().toJson(command_info2));
				HttpUtils.doPOST(url, postData, function,null);
			}
			else {
				String link = links.getPeerCommandLink(node.peer_address(), "codeine", "switch-version", "beta");
				HttpUtils.doGET(link, function,null, HttpUtils.READ_TIMEOUT_MILLI);
			}
			if (result.length() > 0) {
				String finishedMessage = "command " + (success ? "succeeded" : "failed") + " on node " + node.alias();
				if (!shouldOutputImmediatly){
					allNodesCommandExecuter.writeLine(getAnnounceMessage(getHeaderMessage()) + "\n" + result.toString() + "\n" + finishedMessage);
				}
				else {
					announce(finishedMessage);
				}
			} else {
				announce("result is empty for node " + node.alias());
				nodeFailed();
			}
		} catch (Exception ex) {
			announce("error in node " + node.alias() + " message: " + ExceptionUtils.getRootCause(ex).getMessage());
			log.warn("error in node with link " + url + " ; message "
					+ ExceptionUtils.getRootCause(ex).getMessage());
			log.debug("error details", ex);
			nodeFailed();
		}
	}


	private void nodeFailed() {
		if (!failedReported) {
			allNodesCommandExecuter.fail(node);
			failedReported = true;
		}
		else {
			LogUtils.assertFailed(log, "nodeFailed reported more than once");
		}
	}

	private boolean noPermissions() {
		return !userObject.canCommand(project.name(), node.alias());
	}

	private void writeNodeHeader() {
		announce(getHeaderMessage());
	}


	private String getHeaderMessage() {
		return "executed on node: " + node.alias() + ", output below";
	}

	private void announce(String line) {
		allNodesCommandExecuter.writeLine(getAnnounceMessage(line));
	}


	private String getAnnounceMessage(String line) {
		return "===> " + line + " <===";
	}

	private final class ReadCommandOutputFunction implements Function<String, Void> {
		private final StringBuilder result;

		private ReadCommandOutputFunction(StringBuilder result) {
			this.result = result;
		}

		@Override
		public Void apply(String line) {
			Matcher matcher = pattern.matcher(line.replace("\n", ""));
			if (matcher.matches()) {
				int exitStatus = Integer.valueOf(matcher.group(1));
				if (ExitStatus.SUCCESS != exitStatus) {
					nodeFailed();
					line = "\nCommand failed with exit status " + exitStatus;
					switch (exitStatus)	{
					case ExitStatus.TIMEOUT: {
						line += " (timeout)";
						break;
					}
					case ExitStatus.INTERRUPTED: {
						line += " (interrupted)";
						break;
					}
					case ExitStatus.EXCEPTION: {
						line += " (internal error)";
						break;
					}
					case ExitStatus.IO_ERROR: {
						line += " (IO Error)";
						break;
					}
					default: 
						line += " (Definition Missing in PeerCommandWorker)";
						break;
					}
					line += "\n";
				} else {
					allNodesCommandExecuter.nodeSuccess(node);
					success = true;
				}
			}
			if (!success) {//failed or not finished
				if (shouldOutputImmediatly) {
					allNodesCommandExecuter.writeLine(line);
				}
				result.append(line + "\n");
			}
			return null;
		}
	}
}