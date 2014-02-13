package codeine.servlets.api_servlets.angular;

import java.lang.reflect.Type;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.jsons.auth.PermissionsConfJson;
import codeine.jsons.auth.UserPermissions;
import codeine.jsons.global.UserPermissionsJsonStore;
import codeine.servlet.AbstractServlet;
import codeine.servlet.PermissionsManager;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;

@SuppressWarnings("serial")
public class CodeinePermissionsApiServlet extends AbstractServlet {

	private static final Logger log = Logger.getLogger(CodeinePermissionsApiServlet.class);
	private static final long serialVersionUID = 1L;
	private @Inject PermissionsManager permissionsManager;
	private @Inject UserPermissionsJsonStore permissionsJsonStore;
	

	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return permissionsManager.isAdministrator(request);
	}
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		List<UserPermissions> permissions = permissionsJsonStore.get().permissions();
		writeResponseJson(response, permissions);
	}
	
	@Override
	protected void myPut(HttpServletRequest request, HttpServletResponse response) {
		Type listType = new TypeToken<List<UserPermissions>>() { }.getType();
		List<UserPermissions> data = readBodyJson(request, listType);
		log.info("Will update codeine configuration. New Config is: " + data);
		permissionsJsonStore.store(new PermissionsConfJson(data));
		writeResponseJson(response, data);
	}

}
