package codeine.users;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeine.servlet.AbstractServlet;
import codeine.servlet.HtmlHeaderWriter;
import codeine.utils.ExceptionUtils;
import codeine.utils.network.HttpUtils;

import com.google.inject.Inject;

public class RegisterServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Inject
	private HtmlHeaderWriter htmlHeaderWriter;
	@Inject
	private UsersManager usersManager;
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		writeForm(request, response, false);
	}

	private void writeForm(HttpServletRequest request, HttpServletResponse response, boolean wrongPassword) {
		String projectName = request.getParameter("project");
		String path = null != projectName ? projectName + "/ login" : "login";
		htmlHeaderWriter.writeHeader(request, response, projectName, path);
		String line = "";
		
		if (wrongPassword){
			line += "            <alert><div class=\"alertbar\">";
			line += "<label><font color='red'>" + "Password do not match" + "</font></label>";
			line += "<br style=\"clear:left\"/></div></alert>";
		}
		
		line += "            <alert><div class=\"alertbar\"><ul>";
		line += "<li width=\"50px\">" + "User:" + "</li>";
		line += "<li>" + "<input type='text' name='username'/>" + "</li>";
		line += "</ul><br style=\"clear:left\"/></div></alert>";

		line += "            <alert><div class=\"alertbar\"><ul>";
		line += "<li width=\"50px\">" + "Password:" + "</li>";
		line += "<li>" + "<input type='password' name='password'/>" + "</li>";
		line += "</ul><br style=\"clear:left\"/></div></alert>";
		
		line += "            <alert><div class=\"alertbar\"><ul>";
		line += "<li width=\"50px\">" + "Retype password:" + "</li>";
		line += "<li>" + "<input type='password' name='password2'/>" + "</li>";
		line += "</ul><br style=\"clear:left\"/></div></alert>";

		line += "            <alert><div class=\"alertbar\"><ul>";
		line += "<li>" + "<input type='submit' value='Register'/>" + "</li>";
		line += "</ul><br style=\"clear:left\"/></div></alert>";

		getWriter(response).append("<form method='POST' action='/register"+addProjectParam(projectName)+"'>" + line + "</form>");
		htmlHeaderWriter.writeFooter(response);
	}
	
	@Override
	protected void myPost(HttpServletRequest request, HttpServletResponse response) {
		String userName = request.getParameter("username");
	    String password = request.getParameter("password");
	    String password2 = request.getParameter("password2");
	    if (!password.equals(password2)){
	    	writeForm(request, response, true);
	    	return;
	    }
	    MessageDigest m;
		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw ExceptionUtils.asUnchecked(e);
		}
        m.update(password.getBytes(),0,password.length());
        String md5 = "MD5:" + new BigInteger(1,m.digest()).toString(16);
        usersManager.addUser(userName, md5);
        //TODO forward request to somewhere
	}

	private String addProjectParam(String projectName) {
		if (null != projectName){
			return "?project=" + HttpUtils.encode(projectName);
		}
		return "";
	}
}