package codeine.statistics;

import java.io.Serializable;


@SuppressWarnings("unused")
public class MonitorStatusItem implements Serializable{
	private static final long serialVersionUID = 1L;

	private String date;
	private int total;
	private int fail;
	
	public MonitorStatusItem(String date, int success, int fail) {
		this.date = date.toString();
		this.total = success + fail;
		this.fail = fail;
	}
	
	public String date() {
		return date;
	}
	

}
