package codeine.command_peer;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.api.CommandStatusJson;
import codeine.api.NodeWithPeerInfo;
import codeine.configuration.PathHelper;
import codeine.jsons.CommandExecutionStatusInfo;
import codeine.model.Constants;
import codeine.servlet.PrepareForShutdown;
import codeine.utils.FilesUtils;
import codeine.utils.MiscUtils;
import codeine.utils.TextFileUtils;
import codeine.utils.exceptions.InShutdownException;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.inject.Provider;

public class NodesCommandExecuterProvider {

	private static final Logger log = Logger.getLogger(NodesCommandExecuterProvider.class);
	@Inject private Provider<AllNodesCommandExecuter> allNodesCommandExecuterProvider;
	@Inject private PrepareForShutdown prepareForShutdown;
	@Inject private Gson gson;
	@Inject private PathHelper pathHelper;
	private List<AllNodesCommandExecuter> executers = Lists.newArrayList();
	
	public AllNodesCommandExecuter createExecutor() {
		if (prepareForShutdown.isSequnceActivated()) {
			throw new InShutdownException();
		}
		cleanAndGet();
		AllNodesCommandExecuter executer = allNodesCommandExecuterProvider.get();
		synchronized (executers) {
			executers.add(executer);
		}
		return executer;
	}

	public List<CommandStatusJson> getAllCommands(String projectName, String nodeName) {
		List<CommandStatusJson> $ = getActive(projectName, nodeName);
		String parentDir = pathHelper.getAllCommandsInProjectOutputDir(projectName);
		List<String> filesInDir = FilesUtils.getFilesInDir(parentDir);
		for (String dir : filesInDir) {
			if (contains($, dir)){
				continue;
			}
			String file = parentDir + "/" + dir + Constants.JSON_COMMAND_FILE_NAME;
			try {
				CommandExecutionStatusInfo j = gson.fromJson(TextFileUtils.getContents(file), CommandExecutionStatusInfo.class); 
				if (!shouldShowByNode(j, nodeName)){
					continue;
				}
				int size = j.nodes_list().size();
				int successSize = j.success_list().size();
				int failSize = j.fail_list().size();
				int sizeNotZero = size != 0 ? size :  successSize + failSize != 0 ? successSize + failSize : 1;
				int successPercent = successSize * 100 / sizeNotZero;
				int failPercent = failSize * 100 / sizeNotZero;
				String alias = j.nodes_list().size() == 1 ? j.nodes_list().get(0).alias() : null;
				$.add(new CommandStatusJson(j.command(), projectName, size, successPercent, failPercent, j.start_time(), j.id() ,j.finished(), alias));
			} catch (Exception e) {
				log.warn("failed in command " +  dir + " for project " + projectName + " file is '" + file + "' and error is " + e.getMessage());
			}
		}
		Comparator<CommandStatusJson> comp = new Comparator<CommandStatusJson>() {
			@Override
			public int compare(CommandStatusJson o1, CommandStatusJson o2) {
				return (int) (o2.id() - o1.id());
			}
		};
		Collections.sort($, comp);
		return $;
	}
	
	private boolean shouldShowByNode(CommandExecutionStatusInfo j, String nodeName) {
		List<NodeWithPeerInfo> nodes_list = j.nodes_list();
		return shouldShowByNode(nodeName, nodes_list);
	}

	private boolean shouldShowByNode(String nodeName, List<NodeWithPeerInfo> nodes_list) {
		if (nodeName == null) {
			return true;
		}
		for (NodeWithPeerInfo node : nodes_list) {
			if (node.name().equals(nodeName)) {
				return true;
			}
		}
		return false;
	}

	private boolean contains(List<CommandStatusJson> $, String dir) {
		for (CommandStatusJson commandStatusJson : $) {
			if (String.valueOf(commandStatusJson.id()).equals(dir)){
				return true;
			}
		}
		return false;
	}

	private List<CommandStatusJson> getActive(final String projectName, final String nodeName) {
		Predicate<AllNodesCommandExecuter> filter = new Predicate<AllNodesCommandExecuter>() {
			@Override
			public boolean apply(AllNodesCommandExecuter c){
				return c.project().equals(projectName) && shouldShowByNode(c, nodeName);
			}

		};
		return getActive(filter);
	}
	private boolean shouldShowByNode(AllNodesCommandExecuter c, String nodeName) {
		List<NodeWithPeerInfo> nodes_list = c.nodesList();
		return shouldShowByNode(nodeName, nodes_list);
	}

	public List<CommandStatusJson> getActive(Predicate<AllNodesCommandExecuter> filter) {
		return getActiveStatusFromList(Iterables.filter(cleanAndGet(), filter));
	}
	
	private List<CommandStatusJson> getActiveStatusFromList(Iterable<AllNodesCommandExecuter> iterable) {
		List<CommandStatusJson> $ = Lists.newArrayList();
		for (AllNodesCommandExecuter e : iterable) {
			String alias;
			if (e.nodes() == 1) {
				NodeWithPeerInfo nodeWithPeerInfo = e.nodesList().get(0);
				alias = nodeWithPeerInfo.alias();
			} else {
				alias = null;
			}
			$.add(new CommandStatusJson(e.name(), e.project(), e.nodes(), e.success(), e.error(), e.commandData().start_time(), e.commandData().id(),  !e.isActive(), alias));
		}
		return $;
	}

	private List<AllNodesCommandExecuter> cleanAndGet() {
		List<AllNodesCommandExecuter> $ = Lists.newArrayList();
		synchronized (executers) {
			for (Iterator<AllNodesCommandExecuter> iterator = executers.iterator(); iterator.hasNext();) {
				AllNodesCommandExecuter e = (AllNodesCommandExecuter) iterator.next();
				if (e.isActive()){
					$.add(e);
				}
				else {
					iterator.remove();
				}
			}
		}
		return $;
	}

	public void cancel(String project, long id, String username) {
		for (AllNodesCommandExecuter e : cleanAndGet()) {
			if (e.id() == id && e.project().equals(project)){
				log.info("cancel command " + id);
				e.cancel(username);
				return;
			}
		}
		throw new RuntimeException("could not cancel " + id + " " + project);
	}

	public List<CommandStatusJson> getActive() {
		return getActiveStatusFromList(cleanAndGet());
	}

	public AllNodesCommandExecuter getCommandOrNull(String projectName, String commandName) {
		synchronized (executers) {
			for (AllNodesCommandExecuter e : executers) {
				if (MiscUtils.equals(e.project(), projectName) && 
						MiscUtils.equals(String.valueOf(e.id()), commandName)) {
					return e;
				}
			}
		}
		return null;
	}

}
