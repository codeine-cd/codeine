package codeine.servlet;

import javax.servlet.http.HttpServlet;

import com.google.inject.Scopes;
import com.google.inject.servlet.ServletModule;

public abstract class AbstractServletModule extends ServletModule{

	protected void serveMe(String contextPath, Class<? extends HttpServlet> class1)
	{
		bind(class1).in(Scopes.SINGLETON);
		serve(contextPath).with(class1);
	}
}
