package codeine.servlets.api_servlets.angular;

import codeine.jsons.global.UserPermissionsJsonStore;
import codeine.permissions.PermissionsConfJson;
import codeine.permissions.UserPermissions;
import codeine.plugins.CodeineConfModifyPlugin;
import codeine.plugins.CodeineConfModifyPlugin.Step;
import codeine.servlet.AbstractApiServlet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import java.lang.reflect.Type;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class CodeinePermissionsApiServlet extends AbstractApiServlet {

	private static final Logger log = Logger.getLogger(CodeinePermissionsApiServlet.class);
	private static final long serialVersionUID = 1L;
	private @Inject UserPermissionsJsonStore permissionsJsonStore;
	private @Inject CodeineConfModifyPlugin codeineConfModifyPlugin;
	

	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		if (request.getMethod().equals("POST")) {
			if (!isAdministrator(request)) {
				log.info("User can not define new project");
				return false;
			}
			return true;
		}
		return true;
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
		codeineConfModifyPlugin.call(Step.pre, getUser(request).user().username());
		permissionsJsonStore.store(new PermissionsConfJson(data));
		codeineConfModifyPlugin.call(Step.post, getUser(request).user().username());
		writeResponseJson(response, data);
	}

}
