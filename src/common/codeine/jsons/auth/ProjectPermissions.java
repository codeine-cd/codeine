package codeine.jsons.auth;

import java.util.List;

import com.google.common.collect.Lists;

@SuppressWarnings("unused")
public class ProjectPermissions {

	private String username;
	private boolean can_config;
	private List<String> can_command = Lists.newArrayList();
	
	public ProjectPermissions(String username, boolean can_config, List<String> can_command) {
		super();
		this.username = username;
		this.can_config = can_config;
		this.can_command = can_command;
	}
	
	
	
}
