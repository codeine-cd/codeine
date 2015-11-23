package codeine.servlets;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.model.Constants;
import codeine.servlet.AbstractServlet;
import codeine.version.RsyncSourceGetter;

//TODO remove after build codeine 460 in cfengine
/**
 *	peers get the version reported by jenkins from directory
 */
public class RsyncSourceForVersionServlet extends AbstractServlet{

	private static final Logger log = Logger.getLogger(RsyncSourceForVersionServlet.class);

	private static final long serialVersionUID = 1L;

	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		log.error("Called get of RsyncSourceForVersionServlet");
		getWriter(response).write(Constants.NO_VERSION);
	}

	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return true;
	}
}
