package codeine.servlets;

import codeine.model.Constants;
import codeine.servlet.CodeineServletModule;
import codeine.servlet.HealthServlet;
import codeine.servlets.command_backup.CommandNodeServletBackup;

public class CodeinePeerServletModule  extends CodeineServletModule
{

	@Override
	protected void configureServlets()
	{
		serveMe(CommandNodeServlet.class, Constants.COMMAND_NODE_CONTEXT);
		serveMe(CommandNodeServletBackup.class, Constants.COMMAND_NODE_CONTEXT + "-backup");
		serveMe(ReloadConfigurationServlet.class, Constants.RELOAD_CONFIGURATION_CONTEXT);
		serveMe(ProjectStatusServlet.class, Constants.PEER_PROJECT_STATUS_CONTEXT);
		serveMe(HealthServlet.class, Constants.HEALTH_CONTEXT);
	}
}
