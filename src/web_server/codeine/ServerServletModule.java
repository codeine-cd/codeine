package codeine;

import static codeine.model.Constants.apiContext;
import codeine.model.Constants;
import codeine.servlet.CodeineServletModule;
import codeine.servlets.FileGetterServlet;
import codeine.servlets.api_servlets.CancelShutdownApiServlet;
import codeine.servlets.api_servlets.CommandExecutorApiServlet;
import codeine.servlets.api_servlets.CommandLogApiServlet;
import codeine.servlets.api_servlets.DeleteProjectServlet;
import codeine.servlets.api_servlets.MonitorsStatisticsApiServlet;
import codeine.servlets.api_servlets.NodesCommandApiServlet;
import codeine.servlets.api_servlets.PrepareForShutdownApiServlet;
import codeine.servlets.api_servlets.ProjectNodesApiServlet;
import codeine.servlets.api_servlets.ProjectStatusApiServlet;
import codeine.servlets.api_servlets.ProjectTagsApiServlet;
import codeine.servlets.api_servlets.ProjectsListApiServlet;
import codeine.servlets.api_servlets.ReporterServlet;
import codeine.servlets.api_servlets.UpgradeApiServlet;
import codeine.servlets.api_servlets.angular.CodeineConfigurationApiServlet;
import codeine.servlets.api_servlets.angular.CodeinePermissionsApiServlet;
import codeine.servlets.api_servlets.angular.ProjectConfigurationApiServlet;
import codeine.servlets.api_servlets.angular.ProjectsTabsApiServlet;
import codeine.servlets.api_servlets.angular.RuntimeInfoApiServlet;
import codeine.servlets.front_end.CommandOutputServlet;
import codeine.servlets.front_end.ConfigureProjectServlet;
import codeine.servlets.front_end.NewProjectServlet;
import codeine.servlets.front_end.NodeInfoServlet;
import codeine.servlets.front_end.ProgressiveRawOutputServlet;
import codeine.servlets.front_end.ProjectNodes2Servlet;
import codeine.servlets.front_end.ProjectNodesServlet;
import codeine.servlets.front_end.ProjectStatusServlet;
import codeine.servlets.front_end.ProjectsListServlet;
import codeine.servlets.front_end.RawOutputServlet;
import codeine.servlets.front_end.ScheduleCommandServlet;
import codeine.servlets.front_end.UserInfoServlet;
import codeine.servlets.front_end.manage.ConfigureServlet;
import codeine.servlets.front_end.manage.InternalNodeInfoServlet;
import codeine.servlets.front_end.manage.InternalNodesServlet;
import codeine.servlets.front_end.manage.InternalNodesStatusServlet;
import codeine.servlets.version_label.LabelsServlet;
import codeine.servlets.version_label.VersionLabelServlet;
import codeine.users.RegisterServlet;

public class ServerServletModule extends CodeineServletModule
{
	@Override
	protected void configureServlets()
	{
		serveMe(ProjectStatusServlet.class, Constants.PROJECT_STATUS_CONTEXT);
		serveMe(ProjectStatusApiServlet.class, apiContext(Constants.PROJECT_STATUS_CONTEXT));
		serveMe(ReporterServlet.class, apiContext(Constants.REPORTER_CONTEXT));
		serveMe(UpgradeApiServlet.class, Constants.UPGRADE_SERVER_CONTEXT);
		serveMe(CommandLogApiServlet.class, apiContext(Constants.COMMANDS_LOG_CONTEXT));
		serveMe(MonitorsStatisticsApiServlet.class, apiContext(Constants.MONITORS_STATISTICS_CONTEXT));
		serveMe(CommandExecutorApiServlet.class, apiContext(Constants.COMMANDS_EXECUTER_STATUS));
		serveMe(ProjectNodesServlet.class, Constants.PROJECT_NODES_CONTEXT);
		serveMe(ProjectNodes2Servlet.class, Constants.PROJECT_NODES_CONTEXT+"2");
		serveMe(ProjectTagsApiServlet.class, apiContext(Constants.PROJECT_TAGS_CONTEXT));
		serveMe(ConfigureProjectServlet.class, Constants.CONFIGURE_PROJECT_CONTEXT);
		serveMe(DeleteProjectServlet.class, Constants.DELETE_PROJECT_CONTEXT);
		serveMe(NewProjectServlet.class, Constants.NEW_PROJECT_CONTEXT);
		serveMe(NodeInfoServlet.class, Constants.NODE_INFO_CONTEXT);
		serveMe(UserInfoServlet.class, Constants.USER_INFO_CONTEXT);
		serveMe(ProjectNodesApiServlet.class, apiContext(Constants.PROJECT_NODES_CONTEXT));
		serveMe(RawOutputServlet.class, Constants.RAW_OUTPUT_CONTEXT);
		serveMe(CommandOutputServlet.class, Constants.COMMAND_OUTPUT_CONTEXT);
		serveMe(FileGetterServlet.class, Constants.FILE_GETTER_CONTEXT);
		serveMe(ProgressiveRawOutputServlet.class, Constants.PROGRESSIVE_RAW_OUTPUT_CONTEXT);
		serveMe(ProjectsListServlet.class, Constants.PROJECTS_LIST_CONTEXT, "/");
		serveMe(ProjectsListApiServlet.class, apiContext(Constants.PROJECTS_LIST_CONTEXT));
		serveMe(ConfigureServlet.class, Constants.CONFIGURE_CONTEXT);
		serveMe(InternalNodesStatusServlet.class, Constants.CODEINE_STATUS_CONTEXT);
		serveMe(InternalNodeInfoServlet.class, Constants.INTERNAL_NODE_INFO_CONTEXT);
		serveMe(InternalNodesServlet.class, Constants.CODEINE_NODES_CONTEXT);
		serveMe(NodesCommandApiServlet.class, apiContext(Constants.COMMAND_NODES_CONTEXT));
		serveMe(ScheduleCommandServlet.class, Constants.SCHEDULE_COMMAND_CONTEXT);
		serveMe(LabelsServlet.class, Constants.LABELS_CONTEXT);
		serveMe(VersionLabelServlet.class, Constants.LABEL_CONTEXT);
		serveMe(RegisterServlet.class, Constants.REGISTER_CONTEXT);
		serveMe(PrepareForShutdownApiServlet.class, Constants.PREPARE_FOR_SHUTDOWN_CONTEXT);
		serveMe(CancelShutdownApiServlet.class, Constants.CANCEL_SHUTDOWN_CONTEXT);
		serveMe(CodeineConfigurationApiServlet.class, apiContext(Constants.GLOBAL_CONFIGURATION_CONTEXT));
		serveMe(ProjectConfigurationApiServlet.class, apiContext(Constants.PROJECT_CONFIGURATION_CONTEXT));
		serveMe(RuntimeInfoApiServlet.class, apiContext(Constants.SESSION_INFO_CONTEXT));
		serveMe(ProjectsTabsApiServlet.class, apiContext(Constants.PROJECTS_TABS_CONTEXT));
		serveMe(CodeinePermissionsApiServlet.class, apiContext(Constants.PERMISSIONS_CONFIGURATION_CONTEXT));
	}

	
}
