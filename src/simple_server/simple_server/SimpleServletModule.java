package simple_server;

import codeine.servlet.CodeineServletModule;

public class SimpleServletModule extends CodeineServletModule {
	@Override
	protected void configureServlets() {
		serveMe(WelcomeServlet.class, "/welcome");
	}

}
