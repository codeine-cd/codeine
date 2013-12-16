package codeine.servlets.template;

import java.util.List;

import codeine.servlet.ProjectTemplateLink;
import codeine.servlet.TemplateData;

import com.google.common.collect.Lists;

@SuppressWarnings("unused")
public class ProjectListTemplateData extends TemplateData {

	private List<ProjectTemplateLink> project;
	private List<String> views = Lists.newArrayList();
	private List<ProjectsInView> views_projects;
	private boolean isAdmin;
	
	public ProjectListTemplateData(List<ProjectTemplateLink> project, List<ProjectsInView> views_projects, boolean isAdmin) {
		super();
		this.project = project;
		this.views_projects = views_projects;
		this.isAdmin = isAdmin;
		for (ProjectsInView projectsInView : views_projects) {
			views.add(projectsInView.name());
		}
	}

}
