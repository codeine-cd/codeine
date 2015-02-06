package codeine.servlets.api_servlets.projects;


public class CreateNewProjectJson {
	public String project_name;
	public String selected_project;
	public NewProjectType type;
	
	
	public CreateNewProjectJson() {
		super();
	}


	public CreateNewProjectJson(String project_name, String project_to_copy_from) {
		super();
		this.project_name = project_name;
		this.selected_project = project_to_copy_from;
		this.type = NewProjectType.Copy;
	}
	
	
}