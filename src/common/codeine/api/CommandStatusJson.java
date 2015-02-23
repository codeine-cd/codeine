package codeine.api;



/**
 * This class is used for the commands panel (history and running commands)
 */
@SuppressWarnings("unused")
public class CommandStatusJson {

	public String name;
	public String project;
	public int nodes;
	public int successPercent;
	public int failPercent;
	public int skipPercent;
	private long date_long;
	private long id;
	private boolean finished;
	private boolean can_cancel;
	private String one_node_alias;
	private String user;
	
	public CommandStatusJson(String name, String project, int nodes, int successPercent, int failPercent, long date, long id, boolean finished, String one_node_alias,
			String user) {
		super();
		this.name = name;
		this.project = project;
		this.nodes = nodes;
		this.successPercent = successPercent;
		this.failPercent = failPercent;
		this.id = id;
		this.finished = finished;
		this.date_long = date;
		this.one_node_alias = one_node_alias;
		this.user = user;
		this.skipPercent = finished ?  100 - successPercent - failPercent : 0;
	}

	public long id() {
		return id;
	}
	
	public long long_date() {
		return date_long;
	}

	public String project() {
		return project;
	}

	public String name() {
		return name;
	}

	public void can_cancel(boolean can_cancel) {
		this.can_cancel = can_cancel;
	}
	
	public boolean finished() {
		return finished;
	}

	@Override
	public String toString() {
		return "CommandStatusJson [name=" + name + ", project=" + project + ", id=" + id + "]";
	}

	public String user() {
		return user;
	}

	
	
	
}
