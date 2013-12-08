package codeine.statistics;


@SuppressWarnings("unused")
public class MonitorStatusItem {
	private String date;
	private int success;
	private int fail;
	
	public MonitorStatusItem(String date, int success, int fail) {
		this.date = date.toString();
		this.success = success;
		this.fail = fail;
	}
	
	
	
	public String date() {
		return date;
	}
	

}
