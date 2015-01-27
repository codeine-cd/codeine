package codeine.jsons.collectors;

import com.google.gson.annotations.SerializedName;


public class CollectorInfo {

	private String name;
	@SuppressWarnings("unused")
	private String description;
	private String script_content;
	private Integer min_interval;
	@SerializedName("credentials") private String cred;
	private CollectorType type;
	private boolean notification_enabled;
	
	public static enum CollectorType {
		String,Number,Monitor
	}

	public CollectorInfo() {
		super();
	}
	
	
	public CollectorInfo(String name, String script_content, Integer min_interval, String cred, CollectorType type,
			boolean notification_enabled) {
		super();
		this.name = name;
		this.script_content = script_content;
		this.min_interval = min_interval;
		this.cred = cred;
		this.type = type;
		this.notification_enabled = notification_enabled;
	}


	public CollectorInfo(String name, String script_content, CollectorType type) {
		super();
		this.name = name;
		this.script_content = script_content;
		this.type = type;
	}

	public Integer min_interval() {
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

	public String cred() {
		return cred;
	}

	public boolean notification_enabled() {
		return notification_enabled;
	}
	
	
}
