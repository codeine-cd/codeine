package codeine.command_peer;

import java.util.List;

import codeine.api.NodeDataJson;

@SuppressWarnings("unused")
public class ScehudleCommandPostData {
	private Integer duration,concurrency, errorPercentVal;
	private String parameter, commandName, projectName, commandStrategy, durationUnits, ratio;
	private boolean stopOnErrorCheckbox;
	private List<NodeDataJson> nodes;
	
	public String command() {
		return commandName;
	}

	public boolean stopOnError() {
		return stopOnErrorCheckbox;
	}
	
	public String project_name() {
		return projectName;
	}

	public String params() {
		return parameter;
	}
	
	public List<NodeDataJson> nodes() {
		return nodes;
	}

	public int concurrency() {
		return concurrency;
	}
	
	public int duration() {
		return duration;
	}
	
	public int errorPercent() {
		return errorPercentVal;
	}
	
	public CommandExcutionType commandExcutionType() {
		return CommandExcutionType.valueOf(commandStrategy);
	}
	
	public DurationUnits durationUnits() {
		return DurationUnits.valueOf(durationUnits);
	}
	
	public RatioType ratio() {
		return RatioType.valueOf(ratio);
	}

}