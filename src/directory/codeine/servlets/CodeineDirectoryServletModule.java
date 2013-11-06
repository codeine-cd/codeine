package codeine.servlets;

import codeine.model.Constants;
import codeine.servlet.AbstractServletModule;
import codeine.version.VersionsMappingServlet;

public class CodeineDirectoryServletModule  extends AbstractServletModule
{
	@Override
	protected void configureServlets()
	{
		serveMe(Constants.VERSIONS_MAPPING_CONTEXT, VersionsMappingServlet.class);
		serveMe(Constants.RSYNC_SOURCE, RsyncSourceForVersionServlet.class);
	}

}
