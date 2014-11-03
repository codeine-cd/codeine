package codeine.db;

import java.util.Map;

import codeine.jsons.project.ProjectJson;

public interface ProjectsConfigurationConnector {

	Map<String, ProjectJson> getAllProjects();
	void updateProject(ProjectJson project);
	void deleteProject(ProjectJson project);
	String getKey();

}