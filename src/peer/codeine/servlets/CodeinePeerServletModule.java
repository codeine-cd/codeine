package codeine.servlets;

import codeine.model.Constants;
import codeine.servlet.AbstractServletModule;

public class CodeinePeerServletModule  extends AbstractServletModule
{

	@Override
	protected void configureServlets()
	{
		serveMe(Constants.COMMAND_NODE_CONTEXT, CommandNodeServlet.class);
		serveMe(Constants.PROJECT_STATUS_CONTEXT, ProjectStatusServlet.class);
	}


}
