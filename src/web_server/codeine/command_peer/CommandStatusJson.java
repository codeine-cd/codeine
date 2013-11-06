package codeine.command_peer;

import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("unused")
public class CommandStatusJson {

	public String name;
	public String link;
	public String project;
	public int nodes;
	public int successPercent;
	public int failPercent;
	private String date;
	private long id;
	private boolean finished;
	
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
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm - dd/MM/yyyy");
        Date resultdate = new Date(date);
		this.date = sdf.format(resultdate);
	}

	public long id() {
		return id;
	}
	
	
	
}
