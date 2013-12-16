package codeine.servlets.template;

import codeine.servlet.TemplateData;

@SuppressWarnings("unused")
public class ConfirmDeleteProjectTemplateData extends TemplateData {

	private String projectName;

	public ConfirmDeleteProjectTemplateData(String projectName) {
		super();
		this.projectName = projectName;
	}

	

}
