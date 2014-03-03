package codeine.api;

import codeine.utils.StringUtils;


public class CommandStatusJson {

	public String name;
	public String link;
	public String project;
	public int nodes;
	public int successPercent;
	public int failPercent;
	public int skipPercent;
	private String date;
	private long date_long;
	private long id;
	private boolean finished;
	private boolean can_cancel;
	
	public CommandStatusJson(String name, String link, String project, int nodes, int successPercent, int failPercent, long date, long id, boolean finished) {
		super();
		this.name = name;
		this.link = link;
		this.project = project;
		this.nodes = nodes;
		this.successPercent = successPercent;
		this.failPercent = failPercent;
		this.id = id;
		this.finished = finished;
		this.date_long = date;
		this.date = StringUtils.formatDate(date);
		this.skipPercent = finished ?  100 - successPercent - failPercent : 0;
	}

	public long id() {
		return id;
	}
	
	public long long_date() {
		return date_long;
	}
	
	public String date() {
		return date;
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
		return "CommandStatusJson [name=" + name + ", link=" + link + ", project=" + project + ", nodes=" + nodes
				+ ", successPercent=" + successPercent + ", failPercent=" + failPercent + ", date=" + date
				+ ", date_long=" + date_long + ", id=" + id + ", finished=" + finished + ", can_cancel=" + can_cancel
				+ "]";
	}
	
	
}
