package codeine.jsons.auth;

import java.util.Set;

import com.google.common.collect.Sets;

public class UserProjectPermissions extends AbstractUserPermissions{

	private String username;
	private boolean can_config;
	private Set<String> can_command = Sets.newHashSet();
	
	public UserProjectPermissions(String username, boolean can_config, Set<String> can_command) {
		super();
		this.username = username;
		this.can_config = can_config;
		this.can_command = can_command;
	}

	public boolean canRead() {
		return true;
	}
	
	/**
	 * @return true if can command any node
	 */
	public boolean canCommand() {
		return !can_command.isEmpty();
	}

	public boolean canConfigure() {
		return can_config;
	}

	public String username() {
		return username;
	}

	public boolean canCommand(String nodeAlias) {
		return isSetMatch(can_command, nodeAlias);
	}
	
	
	
}
