package codeine.servlets.api_servlets.angular;


import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.ConfigurationManagerServer;
import codeine.permissions.UserPermissionsGetter;
import codeine.servlet.AbstractApiServlet;

public class UpdateDbApiServlet extends AbstractApiServlet {

	
	private static final Logger log = Logger.getLogger(UpdateDbApiServlet.class);
	private static final long serialVersionUID = 1L;

	@Inject private ConfigurationManagerServer configurationManager;
	@Inject private UserPermissionsGetter permissionsManager;
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return permissionsManager.user(request).isAdministrator();	
	}
	
	@Override
	protected void myPost(HttpServletRequest request, HttpServletResponse response) {
		log.info("got post (push) request");
		configurationManager.updateDb();
	}

}
