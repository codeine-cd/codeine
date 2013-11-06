package codeine.servlets.version_label;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.jsons.labels.LabelJsonProvider;
import codeine.servlet.AbstractServlet;

import com.google.inject.Inject;

public class VersionLabelServlet extends AbstractServlet {
	private static final Logger log = Logger.getLogger(VersionLabelServlet.class);
	private static final long serialVersionUID = 1L;

	@Inject	private LabelJsonProvider versionLabelJsonProvider;
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		log.debug("VersionsServlet request");
		String projectName = request.getParameter("project");
		String label = request.getParameter("label");
		getWriter(response).write(versionLabelJsonProvider.versionForLabel(label, projectName));
	}
}
