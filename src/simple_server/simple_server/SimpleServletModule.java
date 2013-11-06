package simple_server;

import codeine.servlet.AbstractServletModule;

public class SimpleServletModule extends AbstractServletModule {
	@Override
	protected void configureServlets() {
		serveMe("/welcome", WelcomeServlet.class);
	}

}
