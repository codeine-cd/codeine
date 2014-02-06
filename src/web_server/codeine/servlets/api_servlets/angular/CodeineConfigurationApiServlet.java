package codeine.servlets.api_servlets.angular;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.servlet.AbstractServlet;

public class CodeineConfigurationApiServlet extends AbstractServlet {

	
	private static final long serialVersionUID = 1L;

	@Inject private GlobalConfigurationJsonStore configurationJsonStore;
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return true;
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		writeResponseJson(response, configurationJsonStore.get());
	}

}
