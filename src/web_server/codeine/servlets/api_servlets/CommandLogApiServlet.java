package codeine.servlets.api_servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeine.command_peer.NodesCommandExecuterProvider;
import codeine.servlet.AbstractServlet;

import com.google.inject.Inject;

public class CommandLogApiServlet extends AbstractServlet {
	private static final long serialVersionUID = 1L;

	@Inject	private NodesCommandExecuterProvider nodesCommandExecuterProvider;
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		String projectName = request.getParameter("project");
		writeResponseJson(response, nodesCommandExecuterProvider.getAllCommands(projectName));
	}

}
