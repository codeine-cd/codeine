package codeine.servlets;

import java.io.PrintWriter;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeine.command_peer.CommandStatusJson;
import codeine.command_peer.NodesCommandExecuterProvider;
import codeine.servlet.AbstractServlet;

public class CommandExecutorStatusServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Inject	private NodesCommandExecuterProvider nodesCommandExecuterProvider;
	
	@Override
	protected void myGet(HttpServletRequest req, HttpServletResponse resp){
		PrintWriter writer = getWriter(resp);
		List<CommandStatusJson> active = nodesCommandExecuterProvider.getActive();
		writer.println(gson().toJson(active));
	}
	
}
