package codeine.servlets.api_servlets;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.model.Constants;
import codeine.servlet.AbstractServlet;
import codeine.servlet.PermissionsManager;
import codeine.servlet.PrepareForShutdown;
import codeine.utils.ExceptionUtils;

import com.google.inject.Inject;

public class PrepareForShutdownApiServlet extends AbstractServlet {
	private static final Logger log = Logger.getLogger(PrepareForShutdownApiServlet.class);
	private static final long serialVersionUID = 1L;

	@Inject private PrepareForShutdown prepareForShutdown;
	@Inject private PermissionsManager permissionsManager;
	
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		log.info("prepare for shutdown initiated by user " + permissionsManager.user(request));
		prepareForShutdown.sequenceActivated(true);
		if ("true".equals(request.getParameter(Constants.UrlParameters.REDIRECT))) {
			try {
				response.sendRedirect(Constants.CONFIGURE_CONTEXT);
			} catch (IOException e) {
				throw ExceptionUtils.asUnchecked(e);
			}
		}
	}

	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return isAdministrator(request);
	}

}
