package codeine.servlet;

import codeine.model.Constants;

public class GeneralServletModule extends CodeineServletModule {

	@Override
	protected void configureServlets()
	{
		serveMe(CodeineInfoServlet.class, Constants.INFO_CONTEXT);
		serveMe(CodeineVersionInfoServlet.class, Constants.VERSION_INFO_CONTEXT);
	}
}
