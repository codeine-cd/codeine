package codeine.jsons.command;

import codeine.api.CommandExcutionType;
import codeine.api.DurationUnits;
import codeine.api.RatioType;
import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class CommandInfo {

    private static final int MAX_CONCURRENCY = 500;
    private String name, description;
    private String project_name;
    @SerializedName("credentials")
    private String cred;
    private Integer timeoutInMinutes;
    private Integer block_after_execution_minutes;
    private Integer duration, concurrency, error_percent_val;
    private CommandExcutionType command_strategy;
    private Boolean stop_on_error;
    private DurationUnits duration_units;
    private RatioType ratio;
    @SuppressWarnings("unused")
    private boolean safe_guard;
    private Boolean prevent_override;
    private List<CommandParameterInfo> parameters = Lists.newArrayList();
    private String script_content;
    private List<String> command_tags = Lists.newArrayList();


    public CommandInfo() {
        super();
    }

    public CommandInfo(String name, String description, String project_name, String cred,
        Integer timeoutInMinutes,
        Integer block_after_execution_minutes, Integer duration, Integer concurrency,
        Integer error_percent_val,
        CommandExcutionType command_strategy, Boolean stop_on_error, DurationUnits duration_units,
        RatioType ratio,
        boolean safe_guard, Boolean prevent_override, List<CommandParameterInfo> parameters,
        String script_content, List<String> command_tags) {
        super();
        this.name = name;
        this.description = description;
        this.project_name = project_name;
        this.cred = cred;
        this.timeoutInMinutes = timeoutInMinutes;
        this.block_after_execution_minutes = block_after_execution_minutes;
        this.duration = duration;
        this.concurrency = concurrency;
        this.error_percent_val = error_percent_val;
        this.command_strategy = command_strategy;
        this.stop_on_error = stop_on_error;
        this.duration_units = duration_units;
        this.ratio = ratio;
        this.safe_guard = safe_guard;
        this.prevent_override = prevent_override;
        this.parameters = parameters;
        this.script_content = script_content;
        this.command_tags = command_tags;
    }


    public CommandInfo(String name, String project_name) {
        this.name = name;
        this.project_name = project_name;
    }


    public String description() {
        return description;
    }

    public String name() {
        return name;
    }

    public String cred() {
        return cred;
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
        return stop_on_error != null && stop_on_error;
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
        return prevent_override != null && prevent_override;
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

    public List<String> command_tags() {
        if (command_tags == null) {
            return Lists.newArrayList();
        }
        return command_tags;
    }

    @Override
    public String toString() {
        return "CommandInfo [name=" + name + "]";
    }

    public void cred(String credentials) {
        this.cred = credentials;
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

    public void overrideByConfiguration(CommandInfo configuredCommand) {
        cred(configuredCommand.cred());
        script_content(configuredCommand.script_content());
        if (null == timeoutInMinutes) {
            timeoutInMinutes = configuredCommand.timeoutInMinutes;
            if (null == timeoutInMinutes) {
                timeoutInMinutes = 10;
            }
        }
        if (null == duration) {
            duration = configuredCommand.duration;
        }
        if (null == concurrency) {
            concurrency = configuredCommand.concurrency;
        }
        if (null == error_percent_val) {
            error_percent_val = configuredCommand.error_percent_val;
        }
        if (null == command_strategy) {
            command_strategy = configuredCommand.command_strategy;
        }
        if (null == stop_on_error) {
            stop_on_error = configuredCommand.stop_on_error;
        }
        if (null == duration_units) {
            duration_units = configuredCommand.duration_units;
        }
        if (null == ratio) {
            ratio = configuredCommand.ratio;
        }
        if (null == prevent_override) {
            prevent_override = configuredCommand.prevent_override;
        }
        if (null == parameters || parameters.isEmpty()) {
            parameters = Lists.newArrayList();
            configuredCommand.parameters.forEach(parameter -> parameters
                .add(new CommandParameterInfo(parameter.name(), parameter.default_value())));
        }
    }

    public void duration(int duration) {
        this.duration = duration;
    }

    public Integer block_after_execution_minutes() {
        return block_after_execution_minutes;
    }


    public int hashCode() {
        return name().hashCode();
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof CommandInfo)) {
            return false;
        }

        CommandInfo other = (CommandInfo) o;
        return this.name().equals(other.name());
    }

}
