package codeine.servlets;

import codeine.model.Constants;
import codeine.servlet.CodeineServletModule;

public class CodeinePeerServletModule  extends CodeineServletModule
{

	@Override
	protected void configureServlets()
	{
		serveMe(CommandNodeServlet.class, Constants.COMMAND_NODE_CONTEXT);
		serveMe(ReloadConfigurationServlet.class, Constants.RELOAD_CONFIGURATION_CONTEXT);
		serveMe(ProjectStatusServlet.class, Constants.PEER_PROJECT_STATUS_CONTEXT);
	}


}
