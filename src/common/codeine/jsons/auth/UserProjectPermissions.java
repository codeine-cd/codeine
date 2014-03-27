package codeine.jsons.auth;

import java.util.List;

import com.google.common.collect.Lists;

public class UserProjectPermissions {

	private String username;
	private boolean can_config;
	private List<String> can_command = Lists.newArrayList();
	
	public UserProjectPermissions(String username, boolean can_config, List<String> can_command) {
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
	
	
	
}
