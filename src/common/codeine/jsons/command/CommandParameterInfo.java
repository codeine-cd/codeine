package codeine.jsons.command;

import java.util.LinkedHashSet;


@SuppressWarnings("unused")
public class CommandParameterInfo {

	private String name;
	private String default_value;
	private String description;
	private String validation_expression;
	private String value;
	private Integer command_line; //for backward
	private CommandParameterType type;
	private LinkedHashSet<String> allowed_values = new LinkedHashSet<String>();
	
	enum CommandParameterType {String, Boolean, Selection, Password}

	public CommandParameterInfo(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String name() {
		return name;
	}
	public String value() {
		return value;
	}
	public LinkedHashSet<String> allowed_values() {
		return allowed_values;
	}
	public void clearPassword() {
		if (type == CommandParameterType.Password) {
			value = "XXXXX";
		}
	}
	public void value(String value) {
		this.value = value;
	}

	public String default_value() {
		return default_value;
	}
}
