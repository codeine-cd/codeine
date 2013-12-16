package codeine.servlets;

import codeine.model.Constants;
import codeine.servlet.CodeineServletModule;
import codeine.version.VersionsMappingServlet;

public class CodeineDirectoryServletModule extends CodeineServletModule {
	@Override
	protected void configureServlets() {
		serveMe(PeerReportServlet.class, Constants.PEER_REPORT_CONTEXT);
		serveMe(VersionsMappingServlet.class, Constants.VERSIONS_MAPPING_CONTEXT);
		serveMe(RsyncSourceForVersionServlet.class, Constants.RSYNC_SOURCE);
	}

}
