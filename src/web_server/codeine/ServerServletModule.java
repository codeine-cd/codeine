package codeine;

import static codeine.model.Constants.apiContext;
import static codeine.model.Constants.apiTokenContext;

import javax.servlet.http.HttpServlet;

import codeine.model.Constants;
import codeine.servlet.CodeineServletModule;
import codeine.servlets.AngularServlet;
import codeine.servlets.RootServlet;
import codeine.servlets.api_servlets.CancelShutdownApiServlet;
import codeine.servlets.api_servlets.CommandExecutorApiServlet;
import codeine.servlets.api_servlets.CommandHistoryApiServlet;
import codeine.servlets.api_servlets.MonitorsStatisticsApiServlet;
import codeine.servlets.api_servlets.NodesCommandApiServlet;
import codeine.servlets.api_servlets.PrepareForShutdownApiServlet;
import codeine.servlets.api_servlets.ProjectStatusApiServlet;
import codeine.servlets.api_servlets.ProjectTagsApiServlet;
import codeine.servlets.api_servlets.ReporterServlet;
import codeine.servlets.api_servlets.UpgradeApiServlet;
import codeine.servlets.api_servlets.angular.CodeineConfigurationApiServlet;
import codeine.servlets.api_servlets.angular.CodeineExperimentalConfigurationApiServlet;
import codeine.servlets.api_servlets.angular.CodeinePermissionsApiServlet;
import codeine.servlets.api_servlets.angular.CollectorStatusApiServlet;
import codeine.servlets.api_servlets.angular.CommandStatusApiServlet;
import codeine.servlets.api_servlets.angular.ManageStatisticsInfoApiServlet;
import codeine.servlets.api_servlets.angular.MonitorStatusApiServlet;
import codeine.servlets.api_servlets.angular.NodeStatusApiServlet;
import codeine.servlets.api_servlets.angular.ProjectCommandsApiServlet;
import codeine.servlets.api_servlets.angular.ProjectConfigurationApiServlet;
import codeine.servlets.api_servlets.angular.ProjectNodesApiServlet;
import codeine.servlets.api_servlets.angular.ProjectStatus2ApiServlet;
import codeine.servlets.api_servlets.angular.ProjectsTabsApiServlet;
import codeine.servlets.api_servlets.angular.RuntimeInfoApiServlet;
import codeine.servlets.api_servlets.angular.UpdateDbApiServlet;
import codeine.servlets.api_servlets.angular.UserInfoApiServlet;
import codeine.servlets.api_servlets.projects.ProjectsListApiServlet;
import codeine.users.RegisterServlet;

import com.google.inject.Scopes;

public class ServerServletModule extends CodeineServletModule
{
	@Override
	protected void configureServlets()
	{
		serveMe(AngularServlet.class, Constants.ANGULAR_CONTEXT, Constants.ANGULAR_WEB_URLS_PATH_SPEC);
		serveMe(RootServlet.class, Constants.ROOT_CONTEXT);
		serveMe(UpgradeApiServlet.class, Constants.UPGRADE_SERVER_CONTEXT);
		serveMe(RegisterServlet.class, Constants.REGISTER_CONTEXT);
		serveMe(PrepareForShutdownApiServlet.class, Constants.PREPARE_FOR_SHUTDOWN_CONTEXT);
		serveMe(CancelShutdownApiServlet.class, Constants.CANCEL_SHUTDOWN_CONTEXT);
		serveApi(ProjectStatusApiServlet.class, (Constants.PROJECT_STATUS_CONTEXT));
		serveApi(ReporterServlet.class, (Constants.REPORTER_CONTEXT));
		serveApi(CommandHistoryApiServlet.class, (Constants.COMMANDS_LOG_CONTEXT));
		serveApi(MonitorsStatisticsApiServlet.class, (Constants.MONITORS_STATISTICS_CONTEXT));
		serveApi(CommandExecutorApiServlet.class, (Constants.COMMANDS_EXECUTER_STATUS));
		serveApi(ProjectTagsApiServlet.class, (Constants.PROJECT_TAGS_CONTEXT));
		serveApi(ProjectsListApiServlet.class, (Constants.PROJECTS_LIST_CONTEXT));
		serveApi(NodesCommandApiServlet.class, (Constants.COMMAND_NODES_CONTEXT));
		serveApi(CodeineConfigurationApiServlet.class, (Constants.GLOBAL_CONFIGURATION_CONTEXT));
		serveApi(UpdateDbApiServlet.class, (Constants.PUSH_PROJECTS_TO_DB_CONTEXT));
		serveApi(CodeineExperimentalConfigurationApiServlet.class, (Constants.EXPERIMENTAL_CONFIGURATION_CONTEXT));
		serveApi(ProjectConfigurationApiServlet.class, (Constants.PROJECT_CONFIGURATION_CONTEXT));
		serveApi(ProjectCommandsApiServlet.class, (Constants.PROJECT_COMMANDS_CONFIGURATION_CONTEXT));
		serveApi(UserInfoApiServlet.class, (Constants.USER_INFO_CONTEXT));
		serveApi(ManageStatisticsInfoApiServlet.class, (Constants.MANAGE_STATISTICS_INFO_CONTEXT));
		serveApi(ProjectStatus2ApiServlet.class, (Constants.PROJECT_STATUS_CONTEXT + "2"));
		serveApi(ProjectNodesApiServlet.class, (Constants.PROJECT_NODES_CONTEXT));
		serveApi(NodeStatusApiServlet.class, (Constants.NODE_STATUS_CONTEXT));
		serveApi(MonitorStatusApiServlet.class, (Constants.MONITOR_STATUS_CONTEXT));
		serveApi(CollectorStatusApiServlet.class, (Constants.COLLECTOR_STATUS_CONTEXT));
		serveApi(CommandStatusApiServlet.class, (Constants.COMMAND_STATUS_CONTEXT));
		serveApi(RuntimeInfoApiServlet.class, (Constants.SESSION_INFO_CONTEXT));
		serveApi(ProjectsTabsApiServlet.class, (Constants.PROJECTS_TABS_CONTEXT));
		serveApi(CodeinePermissionsApiServlet.class, (Constants.PERMISSIONS_CONFIGURATION_CONTEXT));
	}

	private void serveApi(Class<? extends HttpServlet> class1, String contextPath) {
		bind(class1).in(Scopes.SINGLETON);
		serve(apiContext(contextPath), apiTokenContext(contextPath)).with(class1);
	}

	
}
