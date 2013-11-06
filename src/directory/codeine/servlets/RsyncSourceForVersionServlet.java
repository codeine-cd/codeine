package codeine.servlets;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.model.Constants;
import codeine.servlet.AbstractServlet;
import codeine.version.RsyncSourceGetter;

public class RsyncSourceForVersionServlet extends AbstractServlet{

	private static final Logger log = Logger.getLogger(RsyncSourceForVersionServlet.class);

	private static final long serialVersionUID = 1L;
	
	private @Inject RsyncSourceGetter rsyncSourceGetter;
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		String version = request.getParameter("version");
		String hostname = request.getParameter("peer");
		log.info("get with params version " + version + " peer " + hostname);
		String rsyncSource = rsyncSourceGetter.getForVersion(version, hostname);
		log.info("rsync source is " + rsyncSource);
		if (null != rsyncSource){
			getWriter(response).write(rsyncSource);
		}
		else{
			getWriter(response).write(Constants.NO_VERSION);
		}
	}

}
