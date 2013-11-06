package codeine.command_peer;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.configuration.Links;

import com.google.common.collect.Lists;
import com.google.inject.Provider;

public class NodesCommandExecuterProvider {

	private static final Logger log = Logger
			.getLogger(NodesCommandExecuterProvider.class);
	@Inject private Provider<AllNodesCommandExecuter> allNodesCommandExecuterProvider;
	@Inject	private Links links;
	private List<AllNodesCommandExecuter> executers = Lists.newArrayList();
	
	public AllNodesCommandExecuter createExecutor() {
		cleanAndGet();
		AllNodesCommandExecuter executer = allNodesCommandExecuterProvider.get();
		synchronized (executers) {
			executers.add(executer);
		}
		return executer;
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
