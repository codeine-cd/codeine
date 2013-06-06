package yami;

import javax.servlet.http.HttpServlet;

import yami.model.Constants;
import yami.servlets.AggregateNodesServlet;
import yami.servlets.AllNodesCommandServlet;
import yami.servlets.AllPeersRestartServlet;
import yami.servlets.DashboardServlet;
import yami.servlets.PeersDashboardServlet;

import com.google.inject.servlet.ServletModule;

public class YamiServerServletModule extends ServletModule
{
	@Override
	protected void configureServlets()
	{
		serveMe(Constants.AGGREGATE_NODE_CONTEXT, AggregateNodesServlet.class);
		serveMe(Constants.DASHBOARD_CONTEXT, DashboardServlet.class);
		serveMe(Constants.PEERS_DASHBOARD_CONTEXT, PeersDashboardServlet.class);
		serveMe(Constants.RESTART_ALL_PEERS_CONTEXT, AllPeersRestartServlet.class);
		serveMe(Constants.COMMAND_NODE_ALL_CONTEXT, AllNodesCommandServlet.class);
	}

	public void serveMe(String contextPath, Class<? extends HttpServlet> class1)
	{
		bind(class1);
		serve(contextPath).with(class1);
	}
}
