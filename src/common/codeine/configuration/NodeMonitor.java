package codeine.configuration;



public class NodeMonitor
{
	private String name;
	private String script_content;
	private Integer minInterval;
	private String credentials;
	private boolean notification_enabled = true;
	
	public NodeMonitor(){
		
	}
	public NodeMonitor(String name, boolean notification_enabled) {
		this.name = name;
		this.notification_enabled = notification_enabled;
	}
	
	public String credentials() {
		return credentials;
	}

	public Integer minInterval() {
		return minInterval;
	}

	public String name() {
		return name;
	}
	
	public boolean notification_enabled() {
		return notification_enabled;
	}
	
	public void script_content(String content) {
		script_content = content;
	}
	
	public String script_content() {
		return script_content;
	}
	@Override
	public String toString() {
		return "NodeMonitor [name=" + name + ", minInterval=" + minInterval + ", credentials=" + credentials
				+ ", notification_enabled=" + notification_enabled + "]";
	}

	
}
