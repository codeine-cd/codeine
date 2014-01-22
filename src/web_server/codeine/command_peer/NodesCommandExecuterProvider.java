package codeine.command_peer;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.api.CommandStatusJson;
import codeine.configuration.Links;
import codeine.configuration.PathHelper;
import codeine.exceptions.InShutdownException;
import codeine.jsons.CommandExecutionStatusInfo;
import codeine.model.Constants;
import codeine.servlet.PrepareForShutdown;
import codeine.utils.FilesUtils;
import codeine.utils.TextFileUtils;

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
	@Inject	private Links links;
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

	public List<CommandStatusJson> getAllCommands(String projectName) {
		List<CommandStatusJson> $ = getActive(projectName);
		String parentDir = pathHelper.getPluginsOutputDir(projectName);
		List<String> filesInDir = FilesUtils.getFilesInDir(parentDir);
		for (String dir : filesInDir) {
			if (contains($, dir)){
				continue;
			}
			String file = parentDir + "/" + dir + Constants.JSON_COMMAND_FILE_NAME;
			try {
				CommandExecutionStatusInfo j = gson.fromJson(TextFileUtils.getContents(file), CommandExecutionStatusInfo.class); 
				String link = links.getCommandOutputGuiLink(j);
				int size = j.nodes_list().size();
				int successSize = j.success_list().size();
				int failSize = j.fail_list().size();
				int sizeNotZero = size != 0 ? size :  successSize + failSize != 0 ? successSize + failSize : 1;
				int successPercent = successSize * 100 / sizeNotZero;
				int failPercent = failSize * 100 / sizeNotZero;
				$.add(new CommandStatusJson(j.command(), link, projectName, size, successPercent, failPercent, j.start_time() ,j.id(), j.finished()));
			} catch (Exception e) {
				log.warn("failed in command " +  dir + " for project " + projectName + " file is " + file, e);
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
	
	private boolean contains(List<CommandStatusJson> $, String dir) {
		for (CommandStatusJson commandStatusJson : $) {
			if (String.valueOf(commandStatusJson.id()).equals(dir)){
				return true;
			}
		}
		return false;
	}

	private List<CommandStatusJson> getActive(final String projectName) {
		Predicate<CommandStatusJson> filter = new Predicate<CommandStatusJson>() {
			@Override
			public boolean apply(CommandStatusJson c){
				return c.project().equals(projectName);
			}
		};
		return Lists.newArrayList(Iterables.filter(getActive(), filter ));
	}

	public List<CommandStatusJson> getActive() {
		return getActiveStatusFromList(cleanAndGet());
	}
	
	private List<CommandStatusJson> getActiveStatusFromList(List<AllNodesCommandExecuter> list) {
		List<CommandStatusJson> $ = Lists.newArrayList();
		for (AllNodesCommandExecuter e : list) {
			$.add(new CommandStatusJson(e.name(), links.getCommandOutputGuiLink(e.commandData()), e.project(), e.nodes(), e.success(), e.error(), e.commandData().start_time(),  e.commandData().id(),!e.isActive()));
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

	public void cancel(String project, long id) {
		for (AllNodesCommandExecuter e : cleanAndGet()) {
			if (e.id() == id && e.project().equals(project)){
				log.info("cancel command " + e);
				e.cancel();
				return;
			}
		}
		throw new RuntimeException("could not cancel " + id + " " + project);
	}

}
