package codeine.servlets.api_servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.configuration.IConfigurationManager;
import codeine.servlet.AbstractServlet;

import com.google.inject.Inject;

public class ProjectsListApiServlet extends AbstractServlet
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(ProjectsListApiServlet.class);
	private static final long serialVersionUID = 1L;
	@Inject private IConfigurationManager configurationManager;

	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		writeResponseJson(response, configurationManager.getConfiguredProjects());
	}
	
	
}
