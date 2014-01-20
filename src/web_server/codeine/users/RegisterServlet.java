package codeine.users;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;

import codeine.servlet.AbstractServlet;
import codeine.servlet.PermissionsManager;
import codeine.servlet.UsersManager;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.inject.Inject;

public class RegisterServlet extends AbstractServlet {

	private static final Logger log = Logger.getLogger(RegisterServlet.class);

	private static final long serialVersionUID = 1L;

	@Inject
	private UsersManager usersManager;
	@Inject
	private PermissionsManager permissionsManager;
	
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
        usersManager.addUser(r.username, md5);
        if (firstUser) {
        	log.info(r.username + " is the first user, making it admin");
        	permissionsManager.makeAdmin(r.username);
        }
        getWriter(response).write("{}");
	}

	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return true;
	}
}