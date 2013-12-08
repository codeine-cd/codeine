package codeine;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import codeine.configuration.IConfigurationManager;
import codeine.db.ProjectsConfigurationConnector;
import codeine.jsons.nodes.NodesManager;
import codeine.jsons.project.ProjectJson;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class ConfigurationManagerPeer implements IConfigurationManager {
	private static final Logger log = Logger.getLogger(ConfigurationManagerPeer.class);

	private Map<String, ProjectJson> projects;
	private NodesManager nodesManager;
	private ProjectsConfigurationConnector projectsConfigurationGetter;

	@Inject	
	public ConfigurationManagerPeer(NodesManager nodesManager, ProjectsConfigurationConnector projectsConfigurationGetter) {
		this.nodesManager = nodesManager;
		this.projectsConfigurationGetter = projectsConfigurationGetter;
	}



	@Override
	public synchronized void refresh() {
		log.info("refresh configuration.");
		Map<String, ProjectJson> newProjects = getProjectsFromMysql();
		//find changed projects?
		projects = newProjects;
		for (ProjectJson projectJson : newProjects.values()) {
			try {
				nodesManager.init(projectJson);
			} catch (Exception e) {
				log.warn("failed to refresh nodes in project " + projectJson.name(), e);
			}
		}
	}

	private Map<String, ProjectJson> getProjectsFromMysql() {
		return projectsConfigurationGetter.getAllProjects();
	}

	@Override
	public List<ProjectJson> getConfiguredProjects() {
		if (null == projects) {
			refresh();
		}
		return Lists.newArrayList(projects.values());
	}

	@Override
	public ProjectJson getProjectForName(String projectName) {
		if (!projects.containsKey(projectName)){
			throw new IllegalArgumentException("project not found " + projectName);
		}
		return projects.get(projectName);
	}



	@Override
	public boolean hasProject(String projectName) {
		return projects.containsKey(projectName);
	}
}
