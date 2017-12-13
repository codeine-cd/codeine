package codeine.servlets.api_servlets.angular;


import codeine.ConfigurationManagerServer;
import codeine.jsons.collectors.CollectorInfo;
import codeine.jsons.command.CommandInfo;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.permissions.IUserWithPermissions;
import codeine.permissions.UserPermissionsGetter;
import codeine.plugins.AfterProjectModifyPlugin;
import codeine.plugins.AfterProjectModifyPlugin.StatusChange;
import codeine.servlet.AbstractApiServlet;
import codeine.utils.JsonUtils;
import codeine.utils.MiscUtils;
import codeine.utils.StringUtils;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

public class ProjectConfigurationApiServlet extends AbstractApiServlet {

	
	private static final Logger log = Logger.getLogger(ProjectConfigurationApiServlet.class);
	private static final long serialVersionUID = 1L;

	@Inject private ConfigurationManagerServer configurationManager;
	@Inject private UserPermissionsGetter permissionsManager;
	@Inject private AfterProjectModifyPlugin afterProjectModifyPlugin;
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		if (request.getMethod().equals("DELETE")) {
			return permissionsManager.user(request).isAdministrator();	
		}
		if (request.getMethod().equals("PUT")) {
			return canConfigureProject(request);	
		}
		if (request.getMethod().equals("POST")) {
			return canConfigureProject(request);	
		}
		return canReadProject(request);
	}
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		writeResponseJson(response, configurationManager.getProjectForName(getParameter(request, Constants.UrlParameters.PROJECT_NAME)));
	}
	
	
	@Override
	protected void myPut(HttpServletRequest request, HttpServletResponse resp) {
		ProjectJson projectJson = readBodyJson(request, ProjectJson.class);
		if (projectJson.conf_uuid() == null) {
			log.warn("Save configuration request without uuid, will reject");
			throw new RuntimeException("Project configuration object must have conf_uuid value");
		}
		log.info("Updating configuration of " + projectJson.name() + ", new configuration is " + projectJson);
		ProjectJson currentProject = configurationManager.getProjectForName(projectJson.name());
		if (null != currentProject && !isAdministrator(request) && 
				(!MiscUtils.equals(currentProject.nodes_discovery_script(), projectJson.nodes_discovery_script()) || !MiscUtils.equals(currentProject.node_discovery_startegy(), projectJson.node_discovery_startegy()))) {
			log.warn("user tried to change discovery script in " + projectJson.name() + " user " + getUser(request).user().username());
			throw new RuntimeException("only admin can change discovery script");
		}
		if (null != currentProject && !isAdministrator(request) && !checkForRootPermissions(projectJson, currentProject)) {
			log.warn("user tried to change command or collector in " + projectJson.name() + " user " +
					getUser(request).user().username() + " to run as root");
			throw new RuntimeException("only admin can set commands and collectors to run as root");
		}
		boolean exists = configurationManager.updateProject(projectJson);
		afterProjectModifyPlugin.call(projectJson, exists ? StatusChange.modify : StatusChange.add, getUser(request).user().username());
		writeResponseJson(resp,projectJson);
	}

	@Override
	protected void myDelete(HttpServletRequest request, HttpServletResponse response) {
		log.info("got delete request");
		IUserWithPermissions user = permissionsManager.user(request);
		String projectName = getParameter(request, Constants.UrlParameters.PROJECT_NAME);
		log.info("project " + projectName + " user " + user.user().username());
		ProjectJson projectToDelete = JsonUtils.cloneJson(configurationManager.getProjectForName(projectName), ProjectJson.class);
		configurationManager.deleteProject(projectToDelete);
		log.info("Project " + projectToDelete.name() + " was deleted by user " + user);
		afterProjectModifyPlugin.call(projectToDelete, StatusChange.remove, getUser(request).user().username());
		getWriter(response).write("{}");
	}
	
	@Override
	protected void myPost(HttpServletRequest request, HttpServletResponse response) {
		log.info("got post (reload) request");
		IUserWithPermissions user = permissionsManager.user(request);
		String projectName = getParameter(request, Constants.UrlParameters.PROJECT_NAME);
		log.info("reloading project " + projectName + " user " + user.user().username());
		ProjectJson projectJson = configurationManager.reloadProject(projectName);
		writeResponseJson(response, projectJson);
	}

	private boolean checkForRootPermissions(ProjectJson newProjectConf, ProjectJson currentProjectConf) {
		List<CollectorInfo> rootCollectors = Lists.newArrayList(Iterables.filter(newProjectConf.collectors(), rootCollectorPredicate()));
		List<CommandInfo> rootCommands = Lists.newArrayList(Iterables.filter(newProjectConf.commands(), rootCommandPredicate()));
		log.info("updated configuration has " + rootCollectors.size() + " collectors and " + rootCommands.size() +
				" commands running has root");
		List<CollectorInfo> currentRootCollectors = Lists.newArrayList(Iterables.filter(currentProjectConf.collectors(), rootCollectorPredicate()));
		List<CommandInfo> currentRootCommands = Lists.newArrayList(Iterables.filter(currentProjectConf.commands(), rootCommandPredicate()));
		rootCollectors.removeAll(currentRootCollectors);
		if (rootCollectors.size() > 0) {
			log.info("Found " + rootCollectors.size() + " collectors that changed to root permissions " + rootCollectors);
			return false;
		}
		rootCommands.removeAll(currentRootCommands);
		if (rootCommands.size() > 0) {
			log.info("Found " + rootCommands.size() + " collectors that changed to root permissions " + rootCommands);
			return false;
		}
		return true;
	}

	private Predicate<CommandInfo> rootCommandPredicate() {
		return new Predicate<CommandInfo>() {
			@Override
			public boolean apply(CommandInfo commandInfo) {
				return !StringUtils.isEmpty(commandInfo.cred()) && commandInfo.cred().equals("root");
			}
		};
	}

	private Predicate<CollectorInfo> rootCollectorPredicate() {
		return new Predicate<CollectorInfo>() {
			@Override
			public boolean apply(CollectorInfo collectorInfo) {
				return !StringUtils.isEmpty(collectorInfo.cred()) && collectorInfo.cred().equals("root");
			}
		};
	}

}
