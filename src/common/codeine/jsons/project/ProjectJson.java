package codeine.jsons.project;

import java.util.List;
import java.util.Map;

import codeine.api.NodeInfo;
import codeine.configuration.NodeMonitor;
import codeine.jsons.collectors.CollectorInfo;
import codeine.jsons.command.CommandInfo;
import codeine.jsons.nodes.NodeDiscoveryStrategy;
import codeine.permissions.UserProjectPermissions;
import codeine.utils.os.OperatingSystem;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.UUID;

public class ProjectJson
{
	private String name;
	private String description;
	private List<NodeMonitor> monitors = Lists.newArrayList();
	private List<CollectorInfo> collectors = Lists.newArrayList();
	private List<MailPolicyJson> mail = Lists.newArrayList();
	private NodeDiscoveryStrategy node_discovery_startegy = NodeDiscoveryStrategy.Configuration;
	private List<CommandInfo> commands = Lists.newArrayList();
	private List<UserProjectPermissions> permissions = Lists.newArrayList();
	private String nodes_discovery_script;
	private String tags_discovery_script;
	private String version_detection_script;
	private List<NodeInfo> nodes_info = Lists.newArrayList();
	private OperatingSystem operating_system = OperatingSystem.Linux;
	private DiscardOldCommandsJson discard_old_commands = new DiscardOldCommandsJson(1000, null);
	private List<EnvironmentVariable> environment_variables = Lists.newArrayList();
	private List<String> include_project_commands = Lists.newArrayList();
	private UUID conf_uuid;
	
	public ProjectJson(String name) {
		this.name = name;
	}

	public ProjectJson() {
	}


	public NodeMonitor getMonitor(String name)
	{
		for (NodeMonitor c : monitors)
		{
			if (c.name().equals(name))
			{
				return c;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return "ProjectJson [" + (name != null ? "name=" + name + ", " : "")
				+ (description != null ? "description=" + description : "") + "]";
	}

	public List<NodeMonitor> monitors() {
		return monitors;
	}
	
	public List<MailPolicyJson> mail() {
		return mail;
	}

	public NodeDiscoveryStrategy node_discovery_startegy() {
		return node_discovery_startegy;
	}

	public void node_discovery_startegy(NodeDiscoveryStrategy newStrategy)
	{
		node_discovery_startegy = newStrategy;
	}

	public String name() {
		return name;
	}

	public List<CommandInfo> commands() {
		return commands;
	}

	public CommandInfo getCommand(String command) {
		for (CommandInfo commandJson2 : commands()) {
			if (commandJson2.name().equals(command)){
				return commandJson2;
			}
		}
		if (command.equals("switch-version")){
			return new CommandInfo();
		}
		throw new IllegalArgumentException("command " + command + " not found in project " + name());
	}

	public void nodes_discovery_script(String content) {
		this.nodes_discovery_script = content;
		
	}

	public void version_detection_script(String content) {
		this.version_detection_script = content;
	}

	public String version_detection_script() {
		return version_detection_script;
	}

	public void nodes_info(List<NodeInfo> nodes) {
		this.nodes_info = nodes;
	}

	public void name(String name) {
		this.name = name;
	}

	public String nodes_discovery_script() {
		return nodes_discovery_script;
	}

	public List<NodeInfo> nodes_info() {
		return nodes_info;
	}
	
	public String tags_discovery_script() {
		return tags_discovery_script;
	}

	public List<UserProjectPermissions> permissions() {
		return permissions;
	}
	public DiscardOldCommandsJson discard_old_commands() {
		return discard_old_commands;
	}
	public OperatingSystem operating_system() {
		return operating_system == null ? OperatingSystem.Linux : operating_system;
	}
	
	public Map<String, String> environmentVariables() {
		Map<String, String> $ = Maps.newLinkedHashMap();
		for (EnvironmentVariable environmentVariable : environment_variables) {
			$.put(environmentVariable.key(), environmentVariable.value());
		}
		return $;
	}

	public List<CollectorInfo> collectors() {
		return collectors;
	}

	public List<String> include_project_commands() {
		return include_project_commands;
	}

	public String description() {
		return description;
	}

	public UUID conf_uuid() {
		return conf_uuid;
	}

	public void conf_uuid(UUID newUUID) {
		conf_uuid = newUUID;
	}

}
