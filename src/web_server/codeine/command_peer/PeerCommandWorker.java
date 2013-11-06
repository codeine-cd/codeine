package codeine.command_peer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import codeine.api.NodeDataJson;
import codeine.model.Constants;
import codeine.utils.ExceptionUtils;
import codeine.utils.ThreadUtils;
import codeine.utils.network.HttpUtils;

public class PeerCommandWorker implements Runnable {
	private static final Logger log = Logger.getLogger(PeerCommandWorker.class);
	private final String link;
	private NodeDataJson node;
	private AllNodesCommandExecuter allNodesCommandExecuter;

	public PeerCommandWorker(String link, NodeDataJson hostport, AllNodesCommandExecuter allNodesCommandExecuter) {
		this.link = link;
		this.node = hostport;
		this.allNodesCommandExecuter = allNodesCommandExecuter;
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
		try {
			ThreadUtils.sleep(getSleepTime());
			log.info("running worker " + link);
			String result = HttpUtils.doGET(link);
			if (null != result) {
				allNodesCommandExecuter.writeLine("executed on node" + node.node_alias() + ", result:\n" + result);
				Matcher matcher = pattern.matcher(result.replace("\n", ""));
				if (!matcher.matches() || 0 != Integer.valueOf(matcher.group(1))) {
					allNodesCommandExecuter.fail(node);
				} else {
					allNodesCommandExecuter.nodeSuccess(node);
				}
			} else {
				log.warn("result is null");
				allNodesCommandExecuter.fail(node);
			}
		} catch (Exception ex) {
			log.warn("error in restart with link " + link + " ; message "
					+ ExceptionUtils.getRootCause(ex).getMessage());
			log.debug("error details", ex);
			allNodesCommandExecuter.fail(node);
		}
		allNodesCommandExecuter.workerFinished();
	}

}