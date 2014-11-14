package codeine.jsons.collectors;


public class CollectorInfo {

	private String name;
	private String script_content;
	private int min_interval;
	private String credentials;
	private CollectorType type;
	private boolean notification_enabled;
	
	public static enum CollectorType {
		String,Integer,Boolean
	}

	public int min_interval() {
		return min_interval;
	}

	@Override
	public String toString() {
		return "CollectorInfo [" + (name != null ? "name=" + name + ", " : "") + (type != null ? "type=" + type : "")
				+ "]";
	}

	public String name() {
		return name;
	}

	public String script_content() {
		return script_content;
	}

	public CollectorType type() {
		return type;
	}

	public String credentials() {
		return credentials;
	}

	public boolean notification_enabled() {
		return notification_enabled;
	}
	
	
}
