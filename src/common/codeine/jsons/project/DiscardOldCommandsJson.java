package codeine.jsons.project;

public class DiscardOldCommandsJson {

	public static final DiscardOldCommandsJson DISABLED = new DiscardOldCommandsJson();
	
	private boolean enabled;
	private Integer max_commands;
	private Integer max_days;
	
	public DiscardOldCommandsJson(int max_commands, int max_days) {
		enabled = true;
		this.max_commands = max_commands;
		this.max_days = max_days;
	}

	public DiscardOldCommandsJson() {
	}

	public boolean enabled() {
		return enabled;
	}

	public Integer max_commands() {
		return max_commands;
	}

	public Integer max_days() {
		return max_days;
	}
}
