package codeine.users;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;

import codeine.jsons.auth.CodeineUser;
import codeine.jsons.global.UserPermissionsJsonStore;
import codeine.permissions.PermissionsConfJson;
import codeine.servlet.AbstractApiServlet;
import codeine.servlet.UsersManager;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.inject.Inject;

public class RegisterServlet extends AbstractApiServlet {

	private static final Logger log = Logger.getLogger(RegisterServlet.class);

	private static final long serialVersionUID = 1L;

	@Inject
	private UsersManager usersManager;
	@Inject
	private UserPermissionsJsonStore permissionsConfigurationJsonStore;
	
	public static class RegisterJson{
		private String username;
		private String password;
	}
	@Override
	protected void myPost(HttpServletRequest request, HttpServletResponse response) {
		RegisterJson r = readBodyJson(request,RegisterJson.class);
		if (usersManager.isUserExists(r.username)) {
			response.setStatus(HttpStatus.CONFLICT_409);
			return;
		}
		boolean firstUser = !usersManager.hasUsers();
        String p = Hashing.md5().hashString(r.password, Charsets.UTF_8).toString();
        String md5 = "MD5:" + p;
        CodeineUser user = usersManager.addUser(r.username, md5);
        if (firstUser) {
        	log.info(r.username + " is the first user, making it admin");
        	makeAdmin(user);
        }
        getWriter(response).write("{}");
	}

	private void makeAdmin(CodeineUser user) {
		PermissionsConfJson permissionsConfJson = permissionsConfigurationJsonStore.get();
		permissionsConfJson.makeAdmin(user);
		permissionsConfigurationJsonStore.store(permissionsConfJson);
	}
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return true;
	}
}