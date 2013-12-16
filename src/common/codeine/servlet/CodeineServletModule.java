package codeine.servlet;

import javax.servlet.http.HttpServlet;

import com.google.inject.Scopes;
import com.google.inject.servlet.ServletModule;

public abstract class CodeineServletModule extends ServletModule{

	protected void serveMe(Class<? extends HttpServlet> class1, String contextPath, String... morePath) {
		bind(class1).in(Scopes.SINGLETON);
		serve(contextPath, morePath).with(class1);
	}
}
