package codeine.permissions;

import java.util.Set;

import com.google.common.collect.Sets;

public class UserProjectPermissions extends AbstractUserPermissions{

	private String username;
	private boolean can_config;
	private Set<String> can_command = Sets.newHashSet();
	private boolean can_read;
	
	public UserProjectPermissions(String username, boolean can_config, Set<String> can_command, boolean can_read) {
		super();
		this.username = username;
		this.can_config = can_config;
		this.can_command = can_command;
		this.can_read = can_read;
	}

	public boolean canRead() {
		return can_read || canCommand();
	}
	
	/**
	 * @return true if can command any node
	 */
	public boolean canCommand() {
		return !can_command.isEmpty() || canConfigure();
	}

	public boolean canConfigure() {
		return can_config;
	}

	public String username() {
		return username;
	}

	public boolean canCommand(String nodeAlias) {
		return isSetMatch(can_command, nodeAlias) || canConfigure();
	}
	
	
	
}
