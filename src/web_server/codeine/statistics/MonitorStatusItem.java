package codeine.statistics;

import java.io.Serializable;


@SuppressWarnings("unused")
public class MonitorStatusItem implements Serializable{
	private static final long serialVersionUID = 1L;

	private String date;
	private int total;
	private int fail;
	private int nodes;
	private String command_name;
	private long date_long;
	
	public MonitorStatusItem(String date,long date_long, int success, int fail,int nodes, String command_name) {
		this.date = date;
		this.date_long = date_long;
		this.nodes = nodes;
		this.command_name = command_name;
		this.total = success + fail;
		this.fail = fail;
	}
	

}
