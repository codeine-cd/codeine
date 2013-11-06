package codeine.servlet;

import codeine.model.Constants;

public class GeneralServletModule extends AbstractServletModule {

	@Override
	protected void configureServlets()
	{
		serveMe(Constants.INFO_CONTEXT, CodeineInfoServlet.class);
		serveMe(Constants.VERSION_INFO_CONTEXT, CodeineVersionInfoServlet.class);
	}
}
