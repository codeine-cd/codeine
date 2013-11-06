package codeine.jsons.project;

import java.util.List;

import codeine.configuration.HttpCollector;
import codeine.jsons.command.CommandJson;
import codeine.jsons.nodes.NodeDiscoveryStrategy;

import com.google.common.collect.Lists;

public class ProjectJson
{
	private String name;
	private List<HttpCollector> collectors = Lists.newArrayList();
	private List<MailPolicyJson> mail = Lists.newArrayList();
	private NodeDiscoveryStrategy node_discovery_startegy = NodeDiscoveryStrategy.Configuration;
	private List<CommandJson> commands = Lists.newArrayList();
	
	public ProjectJson(String name) {
		this.name = name;
	}

	public ProjectJson() {
	}


	public HttpCollector getCollector(String name1)
	{
		for (HttpCollector c : collectors)
		{
			if (c.name().equals(name1))
			{
				return c;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return "ProjectJson [name=" + name + "]";
	}


	public List<HttpCollector> collectors() {
		return collectors;
	}
	
	public List<MailPolicyJson> mail() {
		return mail;
	}

	public NodeDiscoveryStrategy node_discovery_startegy() {
		return node_discovery_startegy;
	}


	public String name() {
		return name;
	}

	public List<CommandJson> commands() {
		return commands;
	}

	public CommandJson getCommand(String command) {
		for (CommandJson commandJson2 : commands()) {
			if (commandJson2.name().equals(command)){
				return commandJson2;
			}
		}
		if (command.equals("switch-version")){
			return new CommandJson();
		}
		throw new IllegalArgumentException("command " + command + " not found in project " + name());
	}

}
