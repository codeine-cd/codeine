package codeine.servlets.api_servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.permissions.UserPermissionsGetter;
import codeine.servlet.PrepareForShutdown;
import codeine.servlets.api_servlets.angular.RuntimeInfoApiServlet;

import com.google.inject.Inject;

public class PrepareForShutdownApiServlet extends RuntimeInfoApiServlet {
	private static final Logger log = Logger.getLogger(PrepareForShutdownApiServlet.class);
	private static final long serialVersionUID = 1L;

	@Inject private PrepareForShutdown prepareForShutdown;
	@Inject private UserPermissionsGetter permissionsManager;
	
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		log.info("prepare for shutdown initiated by user " + permissionsManager.user(request));
		prepareForShutdown.sequenceActivated(true);
		super.myGet(request, response);
	}

	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return isAdministrator(request);
	}

}
