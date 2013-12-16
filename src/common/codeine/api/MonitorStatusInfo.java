package codeine.api;

public class MonitorStatusInfo {

	private String name;
	private String status;

	public MonitorStatusInfo(String name, String status) {
		this.name = name;
		this.status = status;
		
	}
	
	public String name() {
		return name;
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
