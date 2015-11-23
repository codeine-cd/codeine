package codeine.version;

import java.io.PrintWriter;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.jsons.info.VersionInfo;
import codeine.servlet.AbstractServlet;

//TODO remove after build codeine 460 in cfengine
/**
 * jenkisn report version to directory
 */
public class VersionsMappingServlet extends AbstractServlet {

	private static final Logger log = Logger.getLogger(VersionsMappingServlet.class);

	private static final long serialVersionUID = 1L;

	@Override
	protected void myPost(HttpServletRequest req, HttpServletResponse resp){
		log.error("Called post of VersionsMappingServlet");
	}
	
	@Override
	protected void myGet(HttpServletRequest req, HttpServletResponse resp){
		log.error("Called get of VersionsMappingServlet");
		PrintWriter writer = getWriter(resp);
		writer.println();
	}

	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return true;
	}
	
}
