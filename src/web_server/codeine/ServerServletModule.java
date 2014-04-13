package codeine;

import static codeine.model.Constants.*;
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
import codeine.servlets.api_servlets.angular.CommandStatusApiServlet;
import codeine.servlets.api_servlets.angular.MonitorStatusApiServlet;
import codeine.servlets.api_servlets.angular.NodeStatusApiServlet;
import codeine.servlets.api_servlets.angular.ProjectConfigurationApiServlet;
import codeine.servlets.api_servlets.angular.ProjectStatus2ApiServlet;
import codeine.servlets.api_servlets.angular.ProjectsTabsApiServlet;
import codeine.servlets.api_servlets.angular.RuntimeInfoApiServlet;
import codeine.servlets.api_servlets.angular.UserInfoApiServlet;
import codeine.servlets.api_servlets.projects.ProjectsListApiServlet;
import codeine.users.RegisterServlet;

public class ServerServletModule extends CodeineServletModule
{
	@Override
	protected void configureServlets()
	{
		serveMe(ProjectStatusApiServlet.class, apiContext(Constants.PROJECT_STATUS_CONTEXT));
		serveMe(ReporterServlet.class, apiContext(Constants.REPORTER_CONTEXT));
		serveMe(UpgradeApiServlet.class, Constants.UPGRADE_SERVER_CONTEXT);
		serveMe(CommandHistoryApiServlet.class, apiContext(Constants.COMMANDS_LOG_CONTEXT));
		serveMe(MonitorsStatisticsApiServlet.class, apiContext(Constants.MONITORS_STATISTICS_CONTEXT));
		serveMe(CommandExecutorApiServlet.class, apiContext(Constants.COMMANDS_EXECUTER_STATUS));
		serveMe(ProjectTagsApiServlet.class, apiContext(Constants.PROJECT_TAGS_CONTEXT));
		serveMe(AngularServlet.class, Constants.ANGULAR_CONTEXT, Constants.ANGULAR_WEB_URLS_PATH_SPEC);
		serveMe(RootServlet.class, Constants.ROOT_CONTEXT);
		serveMe(ProjectsListApiServlet.class, apiContext(Constants.PROJECTS_LIST_CONTEXT));
		serveMe(NodesCommandApiServlet.class, apiContext(Constants.COMMAND_NODES_CONTEXT));
		serveMe(RegisterServlet.class, Constants.REGISTER_CONTEXT);
		serveMe(PrepareForShutdownApiServlet.class, Constants.PREPARE_FOR_SHUTDOWN_CONTEXT);
		serveMe(CancelShutdownApiServlet.class, Constants.CANCEL_SHUTDOWN_CONTEXT);
		serveMe(CodeineConfigurationApiServlet.class, apiContext(Constants.GLOBAL_CONFIGURATION_CONTEXT));
		serveMe(CodeineExperimentalConfigurationApiServlet.class, apiContext(Constants.EXPERIMENTAL_CONFIGURATION_CONTEXT));
		serveMe(ProjectConfigurationApiServlet.class, apiContext(Constants.PROJECT_CONFIGURATION_CONTEXT));
		serveMe(UserInfoApiServlet.class, apiContext(Constants.USER_INFO_CONTEXT));
		serveMe(ProjectStatus2ApiServlet.class, apiContext(Constants.PROJECT_STATUS_CONTEXT + "2"));
		serveMe(codeine.servlets.api_servlets.angular.ProjectNodesApiServlet.class, apiContext(Constants.PROJECT_NODES_CONTEXT));
		serveMe(NodeStatusApiServlet.class, apiContext(Constants.NODE_STATUS_CONTEXT));
		serveMe(MonitorStatusApiServlet.class, apiContext(Constants.MONITOR_STATUS_CONTEXT));
		serveMe(CommandStatusApiServlet.class, apiContext(Constants.COMMAND_STATUS_CONTEXT));
		serveMe(RuntimeInfoApiServlet.class, apiContext(Constants.SESSION_INFO_CONTEXT));
		serveMe(ProjectsTabsApiServlet.class, apiContext(Constants.PROJECTS_TABS_CONTEXT));
		serveMe(CodeinePermissionsApiServlet.class, apiContext(Constants.PERMISSIONS_CONFIGURATION_CONTEXT));
	}

	
}
