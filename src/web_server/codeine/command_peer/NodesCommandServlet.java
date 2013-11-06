package codeine.command_peer;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.configuration.Links;
import codeine.model.Constants;
import codeine.servlet.AbstractServlet;
import codeine.servlets.template.HtmlMainTemplate;
import codeine.utils.ExceptionUtils;

import com.google.inject.Inject;

public class NodesCommandServlet extends AbstractServlet {
	private static final Logger log = Logger.getLogger(NodesCommandServlet.class);
	private static final long serialVersionUID = 1L;

	@Inject private Links links;
	@Inject private HtmlMainTemplate htmlMainTemplate;
	@Inject private NodesCommandExecuterProvider allNodesCommandExecuterProvider;

	@Override
	protected void myPost(HttpServletRequest request, HttpServletResponse response) {
		log.debug("NodesCommandServlet request");
		String data = request.getParameter(Constants.UrlParameters.DATA_NAME);
		ScehudleCommandPostData commandData = gson().fromJson(data, ScehudleCommandPostData.class);
		long dir = allNodesCommandExecuterProvider.createExecutor().executeOnAllNodes(commandData);
		try {
			response.sendRedirect(links.getCommandOutputGui(commandData.project_name(), commandData.command(), dir));
		} catch (IOException e) {
			throw ExceptionUtils.asUnchecked(e);
		}
	}
	
	@Override
	protected void myDelete(HttpServletRequest request, HttpServletResponse response) {
		String project = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		String id = request.getParameter(Constants.UrlParameters.COMMAND_ID);
		allNodesCommandExecuterProvider.cancel(project, Long.valueOf(id));
	}

}
