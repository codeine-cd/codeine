package codeine.version;

import java.io.PrintWriter;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.jsons.info.VersionInfo;
import codeine.servlet.AbstractServlet;

public class VersionsMappingServlet extends AbstractServlet {

	private static final Logger log = Logger.getLogger(VersionsMappingServlet.class);

	private static final long serialVersionUID = 1L;

	@Inject	private VersionsMapping versionsMapping;
	@Inject	private VersionsMappingStore versionsMappingStore;

	@Override
	protected void myPost(HttpServletRequest req, HttpServletResponse resp){
		VersionInfo versionInfo = readBodyJson(req, VersionInfo.class);
		log.info("recieved version mapping " + versionInfo);
		versionsMapping.update(versionInfo);
		VersionInfo info2 = gson().fromJson(gson().toJson(versionInfo), VersionInfo.class);
		info2.alias = info2.name;
		versionsMapping.update(info2);
		versionsMappingStore.store();
	}
	
	@Override
	protected void myGet(HttpServletRequest req, HttpServletResponse resp){
		PrintWriter writer = getWriter(resp);
		writer.println(gson().toJson(versionsMapping));
	}

	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return true;
	}
	
}
