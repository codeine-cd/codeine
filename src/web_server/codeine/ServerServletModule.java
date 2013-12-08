package codeine;

import static codeine.model.Constants.apiContext;
import codeine.manage.ConfigSubmitServlet;
import codeine.manage.ConfigureServlet;
import codeine.manage.ManageServlet;
import codeine.model.Constants;
import codeine.servlet.CodeineServletModule;
import codeine.servlets.FileGetterServlet;
import codeine.servlets.api_servlets.CommandExecutorApiServlet;
import codeine.servlets.api_servlets.CommandLogApiServlet;
import codeine.servlets.api_servlets.DeleteProjectServlet;
import codeine.servlets.api_servlets.NodesCommandApiServlet;
import codeine.servlets.api_servlets.ProjectNodesApiServlet;
import codeine.servlets.api_servlets.ProjectStatusApiServlet;
import codeine.servlets.api_servlets.ProjectsListApiServlet;
import codeine.servlets.front_end.ConfigureProjectServlet;
import codeine.servlets.front_end.ConfirmDeleteProjectServlet;
import codeine.servlets.front_end.NewProjectServlet;
import codeine.servlets.front_end.NodeInfoServlet;
import codeine.servlets.front_end.ProgressiveRawOutputServlet;
import codeine.servlets.front_end.ProjectNodesServlet;
import codeine.servlets.front_end.ProjectStatusServlet;
import codeine.servlets.front_end.ProjectsListServlet;
import codeine.servlets.front_end.RawOutputServlet;
import codeine.servlets.front_end.ScheduleCommandServlet;
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
		serveMe(CommandLogApiServlet.class, apiContext(Constants.COMMANDS_LOG_CONTEXT));
		serveMe(CommandExecutorApiServlet.class, apiContext(Constants.COMMANDS_EXECUTER_STATUS));
		serveMe(ProjectNodesServlet.class, Constants.PROJECT_NODES_CONTEXT);
		serveMe(ConfigureProjectServlet.class, Constants.CONFIGURE_PROJECT_CONTEXT);
		serveMe(DeleteProjectServlet.class, Constants.DELETE_PROJECT_CONTEXT);
		serveMe(ConfirmDeleteProjectServlet.class, Constants.CONFIRM_DELETE_PROJECT_CONTEXT);
		serveMe(NewProjectServlet.class, Constants.NEW_PROJECT_CONTEXT);
		serveMe(NodeInfoServlet.class, Constants.NODE_INFO_CONTEXT);
		serveMe(ProjectNodesApiServlet.class, apiContext(Constants.PROJECT_NODES_CONTEXT));
		serveMe(RawOutputServlet.class, Constants.RAW_OUTPUT_CONTEXT);
		serveMe(FileGetterServlet.class, Constants.FILE_GETTER_CONTEXT);
		serveMe(ProgressiveRawOutputServlet.class, Constants.PROGRESSIVE_RAW_OUTPUT_CONTEXT);
		serveMe(ProjectsListServlet.class, Constants.PROJECTS_LIST_CONTEXT, "/");
		serveMe(ProjectsListApiServlet.class, apiContext(Constants.PROJECTS_LIST_CONTEXT));
		serveMe(ManageServlet.class, Constants.MANAGEMENT_CONTEXT);
		serveMe(ConfigureServlet.class, Constants.CONFIGURE_CONTEXT);
		serveMe(ConfigSubmitServlet.class, Constants.CONFIG_SUBMIT_CONTEXT);
		serveMe(NodesCommandApiServlet.class, apiContext(Constants.COMMAND_NODES_CONTEXT));
		serveMe(ScheduleCommandServlet.class, Constants.SCHEDULE_COMMAND_CONTEXT);
		serveMe(LabelsServlet.class, Constants.LABELS_CONTEXT);
		serveMe(VersionLabelServlet.class, Constants.LABEL_CONTEXT);
		serveMe(RegisterServlet.class, Constants.REGISTER_CONTEXT);
	}

	
}
