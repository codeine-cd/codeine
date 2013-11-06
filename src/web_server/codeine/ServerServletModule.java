package codeine;

import javax.servlet.http.HttpServlet;

import codeine.command_peer.NodesCommandServlet;
import codeine.command_peer.ScheduleCommandServlet;
import codeine.manage.ConfigSubmitServlet;
import codeine.manage.ConfigureServlet;
import codeine.manage.ManageServlet;
import codeine.model.Constants;
import codeine.servlets.AggregateNodesServlet;
import codeine.servlets.CommandExecutorStatusServlet;
import codeine.servlets.CommandLogServlet;
import codeine.servlets.DashboardServletV2;
import codeine.servlets.FileGetterServlet;
import codeine.servlets.ProgressiveRawOutputServlet;
import codeine.servlets.ProjectsDashboardServlet;
import codeine.servlets.RawOutputServlet;
import codeine.servlets.version_label.VersionLabelServlet;
import codeine.servlets.version_label.VersionsServlet;
import codeine.users.RegisterServlet;
import codeine.version.CommandToNodeServlet;

import com.google.inject.Scopes;
import com.google.inject.servlet.ServletModule;

public class ServerServletModule extends ServletModule
{
	@Override
	protected void configureServlets()
	{
		serveMe(AggregateNodesServlet.class, Constants.AGGREGATE_NODE_CONTEXT);
		serveMe(CommandLogServlet.class, Constants.COMMANDS_LOG_CONTEXT);
		serveMe(CommandExecutorStatusServlet.class, Constants.COMMANDS_EXECUTER_STATUS);
		serveMe(DashboardServletV2.class, Constants.DASHBOARD_CONTEXT);
		serveMe(RawOutputServlet.class, Constants.RAW_OUTPUT_CONTEXT);
		serveMe(FileGetterServlet.class, Constants.FILE_GETTER_CONTEXT);
		serveMe(ProgressiveRawOutputServlet.class, Constants.PROGRESSIVE_RAW_OUTPUT_CONTEXT);
		serveMe(ProjectsDashboardServlet.class, Constants.PROJECTS_DASHBOARD_CONTEXT, "/");
		serveMe(ManageServlet.class, Constants.MANAGEMENT_CONTEXT);
		serveMe(ConfigureServlet.class, Constants.CONFIGURE_CONTEXT);
		serveMe(ConfigSubmitServlet.class, Constants.CONFIG_SUBMIT_CONTEXT);
		serveMe(NodesCommandServlet.class, Constants.COMMAND_NODES_CONTEXT);
		serveMe(ScheduleCommandServlet.class, Constants.SCHEDULE_COMMAND_CONTEXT);
		serveMe(VersionsServlet.class, Constants.LABELS_CONTEXT);
		serveMe(VersionLabelServlet.class, Constants.LABEL_CONTEXT);
		serveMe(CommandToNodeServlet.class, Constants.COMMAND_NODE_IN_SERVER_CONTEXT);
		serveMe(RegisterServlet.class, Constants.REGISTER_CONTEXT);
	}

	public void serveMe(Class<? extends HttpServlet> class1, String contextPath, String... morePath)
	{
		bind(class1).in(Scopes.SINGLETON);
		serve(contextPath, morePath).with(class1);
	}
}
