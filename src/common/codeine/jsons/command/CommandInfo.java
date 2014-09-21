package codeine.jsons.command;

import java.util.List;

import codeine.api.CommandExcutionType;
import codeine.api.DurationUnits;
import codeine.api.RatioType;

import com.google.common.collect.Lists;


public class CommandInfo 
{
    private static final int MAX_CONCURRENCY = 500;    
    private String name, title;
    private String project_name;
    private String credentials;
    private int timeoutInMinutes = 10;
    private Integer duration, concurrency, error_percent_val;
	private CommandExcutionType command_strategy;
	private boolean stop_on_error;
	private DurationUnits duration_units;
	private RatioType ratio;
	private boolean prevent_override = true;
	private List<CommandParameterInfo> parameters = Lists.newArrayList();
	private String script_content;

    public String title(){
    	return title == null ? name : title;
    }

	public String name() {
		return name;
	}

	public String credentials() {
		return credentials;
	}
	public int timeoutInMinutes() {
		return timeoutInMinutes;
	}

	public Integer concurrency() {
		return Math.min(concurrency, MAX_CONCURRENCY);
	}

	public String project_name() {
		return project_name;
	}

	public boolean stop_on_error() {
		return stop_on_error;
	}

	public Integer error_percent_val() {
		return error_percent_val;
	}

	public DurationUnits duration_units() {
		return duration_units;
	}

	public Integer duration() {
		return duration;
	}

	public String command_name() {
		return name;
	}

	public CommandExcutionType command_strategy() {
		return command_strategy;
	}
	
	public boolean prevent_override() {
		return prevent_override;
	}
	
	public List<CommandParameterInfo> parameters() {
		return parameters;
	}
	
	public void script_content(String content) {
		this.script_content = content;
	}
	
	public String script_content() {
		return script_content; 
	}

	@Override
	public String toString() {
		return "CommandInfo [name=" + name + "]";
	}

	public void credentials(String credentials) {
		this.credentials = credentials;
	}

	public RatioType ratio() {
		return ratio;
	}

	public void project_name(String projectName) {
		this.project_name = projectName;
	}

	public void timeoutInMinutes(int timeoutInMinutes) {
		this.timeoutInMinutes = timeoutInMinutes;
	}

	
}
