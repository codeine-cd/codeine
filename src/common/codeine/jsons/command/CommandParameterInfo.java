package codeine.jsons.command;

import java.util.List;


@SuppressWarnings("unused")
public class CommandParameterInfo {

	private String name;
	private String default_value;
	private String description;
	private String validation_expression;
	private String value;
	private Integer command_line; //for backward
	private CommandParameterType type;
	private List<String> allowed_values;
	
	enum CommandParameterType {String, Boolean, Selection}

	public String name() {
		return name;
	}
	public String value() {
		return value;
	}
}
