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
import codeine.utils.ExceptionUtils;
import codeine.utils.ThreadUtils;
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

	public PeerCommandWorker(NodeWithPeerInfo node, AllNodesCommandExecuter allNodesCommandExecuter, CommandInfo command_info, boolean shouldOutputImmediatly, Links links, ProjectJson project) {
		this.node = node;
		this.allNodesCommandExecuter = allNodesCommandExecuter;
		this.command_info = command_info;
		this.shouldOutputImmediatly = shouldOutputImmediatly;
		this.links = links;
		this.project = project;
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
	
	private Pattern pattern = Pattern.compile(".*" + Constants.COMMAND_RESULT + "(-?\\d+).*");
	

	private void execute() {
		String url = links.getPeerLink(node.peer_address()) + Constants.COMMAND_NODE_CONTEXT;
		log.info("commandNode url is " + url);
		try {
			ThreadUtils.sleep(getSleepTime());
			log.info("running worker " + node);
			final StringBuilder result = new StringBuilder();
			Function<String, Void> function = new Function<String, Void>(){
				@Override
				public Void apply(String line) {
					Matcher matcher = pattern.matcher(line.replace("\n", ""));
					if (matcher.matches()) {
						if (0 != Integer.valueOf(matcher.group(1))) {
							allNodesCommandExecuter.fail(node);
						} else {
							allNodesCommandExecuter.nodeSuccess(node);
							success = true;
						}
					}
					else {
						if (shouldOutputImmediatly) {
							allNodesCommandExecuter.writeLine(line);
						}
						result.append(line + "\n");
					}
					return null;
				}
			};
			if (shouldOutputImmediatly){
				writeNodeHeader();
			}
			if (POST && !command_info.name().equals("upgrade_old_peers")) {
				CommandInfoForSpecificNode command_info2 = new CommandInfoForSpecificNode(node.name(), node.alias());
				String postData = UrlParameters.DATA_NAME + "=" + HttpUtils.encodeURL(new Gson().toJson(command_info))
						+"&" + UrlParameters.DATA_ADDITIONAL_COMMAND_INFO_NAME + "=" + HttpUtils.encodeURL(new Gson().toJson(command_info2));
				HttpUtils.doPOST(url, postData, function,null);
			}
			else {
				String link = links.getPeerCommandLink(node.peer_address(), "codeine", "switch-version", "beta");
				HttpUtils.doGET(link, function,null);
			}
			if (result.length() > 0) {
				if (!shouldOutputImmediatly){
					writeNodeHeader();
					allNodesCommandExecuter.writeLine(result.toString());
				}
				announce("command " + (success ? "succeeded" : "failed") + " on node " + node.alias());
			} else {
				announce("result is empty for node " + node.alias());
				allNodesCommandExecuter.fail(node);
			}
		} catch (Exception ex) {
			announce("error in node " + node.alias() + " message: " + ExceptionUtils.getRootCause(ex).getMessage());
			log.warn("error in restart with link " + url + " ; message "
					+ ExceptionUtils.getRootCause(ex).getMessage());
			log.debug("error details", ex);
			allNodesCommandExecuter.fail(node);
		}
		allNodesCommandExecuter.workerFinished();
	}

	private void writeNodeHeader() {
		announce("executed on node: " + node.alias() + ", output below");
	}

	private void announce(String line) {
		allNodesCommandExecuter.writeLine("===> " + line + " <===");
	}

}