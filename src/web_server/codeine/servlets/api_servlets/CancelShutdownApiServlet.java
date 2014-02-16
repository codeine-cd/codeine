package codeine.servlets.api_servlets;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.model.Constants;
import codeine.servlet.PermissionsManager;
import codeine.servlet.PrepareForShutdown;
import codeine.servlets.api_servlets.angular.RuntimeInfoApiServlet;
import codeine.utils.ExceptionUtils;

import com.google.inject.Inject;

public class CancelShutdownApiServlet extends RuntimeInfoApiServlet {
	private static final Logger log = Logger.getLogger(CancelShutdownApiServlet.class);
	private static final long serialVersionUID = 1L;

	@Inject private PrepareForShutdown prepareForShutdown;
	@Inject private PermissionsManager permissionsManager;
	
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		log.info("prepare for shutdown canceled by user " + permissionsManager.user(request));
		prepareForShutdown.sequenceActivated(false);
		Boolean redirect = Boolean.valueOf(request.getParameter(Constants.UrlParameters.REDIRECT));
		if (redirect) {
			try {
				response.sendRedirect(Constants.CONFIGURE_CONTEXT);
			} catch (IOException e) {
				throw ExceptionUtils.asUnchecked(e);
			}
		}
		else {
			super.myGet(request, response);
		}
	}

	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return isAdministrator(request);
	}

}
