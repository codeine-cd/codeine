package yami;

import javax.servlet.http.HttpServlet;

import yami.model.Constants;

import com.google.inject.Scopes;
import com.google.inject.servlet.ServletModule;

public class YamiPeerServletModule  extends ServletModule
{

	@Override
	protected void configureServlets()
	{
		serveMe(Constants.RESTART_CONTEXT, PeerRestartServlet.class);
		serveMe(Constants.COMMAND_NODE_CONTEXT, CommandNodeServlet.class);
	}

	public void serveMe(String contextPath, Class<? extends HttpServlet> class1)
	{
		bind(class1).in(Scopes.SINGLETON);
		serve(contextPath).with(class1);
	}

}
