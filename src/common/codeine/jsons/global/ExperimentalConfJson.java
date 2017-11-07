package codeine.jsons.global;


public class ExperimentalConfJson {

	private String new_issue_link;
	private boolean readonly_web_server;
	private boolean allow_concurrent_commands_in_peer;
	private String groups_plugin;
	private String after_project_modify_plugin;
	private String codeine_conf_modify_plugin;

	public String new_issue_link() {
		return new_issue_link;
	}
	public boolean readonly_web_server() {
		return readonly_web_server;
	}
	public boolean allow_concurrent_commands_in_peer() {
		return allow_concurrent_commands_in_peer;
	}
	public String groups_plugin() {
		return groups_plugin;
	}
	public String after_project_modify_plugin() {
		return after_project_modify_plugin;
	}
	public String codeine_conf_modify_plugin() {
		return codeine_conf_modify_plugin;
	}

}
