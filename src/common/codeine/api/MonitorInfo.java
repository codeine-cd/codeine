package codeine.api;

public class MonitorInfo {

	private String name;
	private String alias;
	private String status;

	public MonitorInfo(String name, String alias, String status) {
		this.name = name;
		this.alias = alias;
		this.status = status;
		
	}
	
	public String name() {
		return name;
	}
	
	public String alias() {
		return alias;
	}
	
	public String status() {
		return status;
	}

	public boolean fail() {
		return status.equals("false");
	}
	public boolean pass() {
		return status.equals("true");
	}
}
