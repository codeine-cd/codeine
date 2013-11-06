package codeine.servlets.template;

import java.util.List;

import codeine.servlet.ProjectTemplateLink;
import codeine.servlet.TemplateData;

@SuppressWarnings("unused")
public class ProjectListTemplateData extends TemplateData {

	private List<ProjectTemplateLink> project;
	
	public ProjectListTemplateData(List<ProjectTemplateLink> project) {
		super();
		this.project = project;
	}

}
