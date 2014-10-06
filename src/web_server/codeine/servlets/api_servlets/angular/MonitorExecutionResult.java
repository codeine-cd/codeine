package codeine.servlets.api_servlets.angular;

@SuppressWarnings("unused")
public class MonitorExecutionResult {

	private int exit_Status;
	private String output;
	private boolean json_format;
	private long total_time, finish_time;

	public MonitorExecutionResult(String output) {
		this.output = output;
	}

	public MonitorExecutionResult(int exit_Status, String output, long total_time, long finish_time) {
		super();
		this.exit_Status = exit_Status;
		this.output = output;
		this.total_time = total_time;
		this.finish_time = finish_time;
		this.json_format = true;
	}

	
}
