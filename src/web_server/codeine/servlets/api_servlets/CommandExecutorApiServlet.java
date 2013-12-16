package codeine.servlets.api_servlets;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeine.command_peer.NodesCommandExecuterProvider;
import codeine.servlet.AbstractServlet;

public class CommandExecutorApiServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Inject	private NodesCommandExecuterProvider nodesCommandExecuterProvider;
	
	@Override
	protected void myGet(HttpServletRequest req, HttpServletResponse response){
		writeResponseJson(response, nodesCommandExecuterProvider.getActive());
	}
	
}
