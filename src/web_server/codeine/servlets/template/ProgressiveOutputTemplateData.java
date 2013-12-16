package codeine.servlets.template;

import codeine.servlet.TemplateData;

@SuppressWarnings("unused")
public class ProgressiveOutputTemplateData extends TemplateData {

	private String command;
	private String path;
	private String projectName;

	public ProgressiveOutputTemplateData(String projectName, String command, String path) {
		
		super();
		this.command = command;
		this.path = path;
		this.projectName = projectName;
	}

}
