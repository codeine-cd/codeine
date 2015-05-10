package codeine.servlets.api_servlets;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.api.NodeGetter;
import codeine.api.NodeWithMonitorsInfo;
import codeine.model.Constants;
import codeine.servlet.AbstractApiServlet;

import com.google.inject.Inject;

public class NodesCsvApiServlet extends AbstractApiServlet {
	private static final Logger log = Logger.getLogger(NodesCsvApiServlet.class);
	private static final long serialVersionUID = 1L;

	@Inject private NodeGetter nodeGetter;
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return canReadProject(request);
	}
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		log.info("myGet");
		String project = getProjectName(request);
		List<NodeWithMonitorsInfo> nodes = nodeGetter.getNodes(project);
		response.setContentType("text/csv");
		PrintWriter writer = getWriter(response);
		writer.println("name,alias");
		for (NodeWithMonitorsInfo n : nodes) {
			writer.println(n.name() + "," + n.alias());
		}
	}
	
	private String getProjectName(HttpServletRequest request) {
		String projectName = getParameter(request, Constants.UrlParameters.PROJECT_NAME);
		return projectName;
	}

	
}
