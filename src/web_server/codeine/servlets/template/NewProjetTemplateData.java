package codeine.servlets.template;


import java.util.List;

import codeine.servlet.TemplateData;

@SuppressWarnings("unused")
public class NewProjetTemplateData extends TemplateData {
	
	private List<String> projects;

	public NewProjetTemplateData(List<String> projects) {
		this.projects = projects;
	}
	

}
