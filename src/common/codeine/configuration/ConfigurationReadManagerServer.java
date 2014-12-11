package codeine.configuration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.jsons.command.CommandInfo;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.utils.FilesUtils;
import codeine.utils.JsonFileUtils;
import codeine.utils.JsonUtils;
import codeine.utils.exceptions.ProjectNotFoundException;
import codeine.utils.logging.LogUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;

public class ConfigurationReadManagerServer implements IConfigurationManager
{
	private static final Logger log = Logger.getLogger(ConfigurationReadManagerServer.class);
	
	public static final ProjectJson NODES_INTERNAL_PROJECT = new ProjectJson(Constants.CODEINE_NODES_PROJECT_NAME);
	
	
	private Map<String, ProjectJson> projects = Maps.newConcurrentMap();
	private JsonFileUtils jsonFileUtils;
	private PathHelper pathHelper;
	
	@Inject
	public ConfigurationReadManagerServer(JsonFileUtils jsonFileUtils, PathHelper pathHelper)
	{
		this.jsonFileUtils = jsonFileUtils; 
		this.pathHelper = pathHelper;
		refresh();
	}

	@Override
	public void refresh() {
		Map<String, ProjectJson> projects1 = Maps.newConcurrentMap();
		try {
			String projectsDir = pathHelper.getProjectsDir();
			log.info("loading configuration, projects from " + projectsDir);
			List<String> files = FilesUtils.getFilesInDir(projectsDir);
			for (String file : files) {
				try {
					if (file.startsWith(".")){
						log.info("will ignore project dir " + file);
						continue;
					}
					String file2 = pathHelper.getProjectsDir() + "/" + file + "/" + Constants.PROJECT_CONF_FILE;
					if (!FilesUtils.exists(file2)) {
						log.info("conf file not exists " + file2);
						continue;
					}
					ProjectJson projectJson = jsonFileUtils.getConfFromFile(file2, ProjectJson.class);
					projects1.put(projectJson.name(), projectJson);
					FilesUtils.mkdirs(pathHelper.getAllCommandsInProjectOutputDir(projectJson.name()));
				} catch (Exception e) {
					log.error("failed to configure project " + file, e);
				}
			}
		} catch (RuntimeException e) {
			log.error("error", e);
			throw e;
		}
		projects(projects1);
	}

	@Override
	public List<ProjectJson> getConfiguredProjects() {
		return Lists.newArrayList(projects().values());
	}
	
	private ProjectJson getProjectForNameOrNull(String projectName) {
		if (Constants.CODEINE_NODES_PROJECT_NAME.equals(projectName)) {
			return NODES_INTERNAL_PROJECT;
		}
		List<ProjectJson> configuredProjects = getConfiguredProjects();
		for (ProjectJson projectJson : configuredProjects) {
			if (projectName.equals(projectJson.name())){
				return projectJson;
			}
		}
		return null;
	}
	@Override
	public ProjectJson getProjectForName(String projectName) {
		ProjectJson $ = getProjectForNameOrNull(projectName);
		if (null == $) {
			throw new ProjectNotFoundException(projectName);
		}
		return $;
	}

	public Map<String, ProjectJson> projects() {
		return projects;
	}

	public void projects(Map<String, ProjectJson> projects) {
		LogUtils.assertTrue(log, projects instanceof ConcurrentHashMap, "created a map that is not concurrent");
		this.projects = projects;
	}

	@Override
	public boolean hasProject(String projectName) {
		return projects.containsKey(projectName);
	}

	public CommandInfo getCommandOfProject(String projectName, String command_name) {
		List<CommandInfo> commands = getProjectCommands(projectName);
		for (CommandInfo c : commands) {
			if (c.name().equals(command_name)){
				return c;
			}
		}
		throw new IllegalArgumentException("command not found " + projectName + " " + command_name);
	}
	
	public List<CommandInfo> getProjectCommands(String projectName) {
		List<CommandInfo> $ = Lists.newArrayList();
		ProjectJson project = getProjectForName(projectName);
		$.addAll(project.commands());
		if (!project.include_project_commands().isEmpty()) {
			for (String p : project.include_project_commands()) {
				ProjectJson projectForNameOrNull = getProjectForNameOrNull(p);
				if (null == projectForNameOrNull) {
					log.warn("project not found " + p + " to include from project " + projectName);
					continue;
				}
				$.addAll(projectForNameOrNull.commands());
			}
		}
		List<CommandInfo> clonedList = JsonUtils.cloneJson($, new TypeToken<List<CommandInfo>>(){}.getType());
		for (CommandInfo commandInfo : clonedList) {
			commandInfo.project_name(projectName);
		}
		return clonedList;
	}
}
