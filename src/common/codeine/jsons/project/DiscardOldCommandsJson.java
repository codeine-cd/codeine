package codeine.jsons.project;

public class DiscardOldCommandsJson {

	public static final DiscardOldCommandsJson DISABLED = new DiscardOldCommandsJson();
	
	private boolean enabled;
	private Integer max_commands;
	private Integer max_days;
}
