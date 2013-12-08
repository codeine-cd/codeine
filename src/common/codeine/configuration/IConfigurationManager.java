package codeine.configuration;

import java.util.List;

import codeine.jsons.project.ProjectJson;

public interface IConfigurationManager {

	void refresh();
	ProjectJson getProjectForName(String projectName);
	List<ProjectJson> getConfiguredProjects();
	boolean hasProject(String project);

}
