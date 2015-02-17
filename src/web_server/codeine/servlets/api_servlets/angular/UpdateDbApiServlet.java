package codeine.servlets.api_servlets.angular;


import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.ConfigurationManagerServer;
import codeine.model.Constants;
import codeine.permissions.UserPermissionsGetter;
import codeine.servlet.AbstractApiServlet;
import codeine.utils.StringUtils;

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
		String address = getParameter(request, Constants.UrlParameters.ADDRESS);
		log.info("got post (push) request, address: " + address);
		if (StringUtils.isEmpty(address)) {
			configurationManager.updateDb();
		}
		else {
			configurationManager.updateDb(address);
		}
	}

}
