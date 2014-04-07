package codeine.statistics;

import java.io.Serializable;


@SuppressWarnings("unused")
public class MonitorStatusItem implements Serializable{
	private static final long serialVersionUID = 1L;

	private String date;
	private int total;
	private int fail;
	private long date_long;
	
	public MonitorStatusItem(String date,long date_long, int success, int fail) {
		this.date = date;
		this.date_long = date_long;
		this.total = success + fail;
		this.fail = fail;
	}
	

}
