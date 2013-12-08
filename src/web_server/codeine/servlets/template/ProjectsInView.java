package codeine.servlets.template;

import java.util.List;

import codeine.servlet.ProjectTemplateLink;

@SuppressWarnings("unused")
public class ProjectsInView {

	private String name;
	private List<ProjectTemplateLink> project_in_view;

	public ProjectsInView(String name, List<ProjectTemplateLink> projects) {
		this.name = name;
		this.project_in_view = projects;
	}

	public String name() {
		return name;
	}

}
