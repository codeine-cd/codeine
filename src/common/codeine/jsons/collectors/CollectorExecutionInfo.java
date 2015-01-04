package codeine.jsons.collectors;

import codeine.jsons.collectors.CollectorInfo.CollectorType;
import codeine.utils.StringUtils;

public class CollectorExecutionInfo {

	private static final int MAX_SIZE = 100;
	private String name;
	private CollectorType type;
	private String value;
	private long execution_duration;
	private long start_time;
	private int exit_status;
	
	public CollectorExecutionInfo(String name, CollectorType type, int exit_status, String value, long execution_duration,
			long start_time) {
		this.name = name;
		this.type = type;
		this.exit_status = exit_status;
		if (!StringUtils.isEmpty(value) && value.length() > MAX_SIZE) {
			this.value = value.substring(0, MAX_SIZE);
		}
		else {
			this.value = value;
		}
		this.execution_duration = execution_duration;
		this.start_time = start_time;
	}

	public String name() {
		return name;
	}
	public CollectorType type() {
		return type;
	}

	public String value() {
		return value;
	}

	@Override
	public String toString() {
		return "CollectorExecutionInfo [name=" + name + ", type=" + type + ", value=" + value + ", exit_status="
				+ exit_status + ", start_time=" + start_time + ", execution_duration=" + execution_duration + "]";
	}

	public boolean isSuccess() {
		return exit_status == 0;
	}

	public String valueAndExitStatus() {
		return "[val=" + value + ",exi="
				+ exit_status + "]";
	}

	public int exit_status() {
		return exit_status;
	}

	public long execution_duration() {
		return execution_duration;
	}

	public long start_time() {
		return start_time;
	}
	
	
}
