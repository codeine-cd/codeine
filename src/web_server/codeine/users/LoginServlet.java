package codeine.users;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeine.servlet.AbstractServlet;
import codeine.servlet.HtmlHeaderWriter;

import com.google.inject.Inject;

public class LoginServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Inject
	private HtmlHeaderWriter htmlHeaderWriter;

	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		request.getSession().setAttribute("returnUrl", request.getParameter("from"));
		String projectName = request.getParameter("project");
		String path = null != projectName ? projectName + "/ login" : "login";
		htmlHeaderWriter.writeHeader(request, response, projectName, path);
		String line = "            <alert><div class=\"alertbar\"><ul>";
		line += "<li width=\"50px\">" + "User:" + "</li>";
		line += "<li>" + "<input type='text' name='j_username'/>" + "</li>";
		line += "</ul><br style=\"clear:left\"/></div></alert>";

		line += "            <alert><div class=\"alertbar\"><ul>";
		line += "<li width=\"50px\">" + "Password:" + "</li>";
		line += "<li>" + "<input type='password' name='j_password'/>" + "</li>";
		line += "</ul><br style=\"clear:left\"/></div></alert>";

		line += "            <alert><div class=\"alertbar\"><ul>";
		line += "<li>" + "<input type='submit' value='Login'/>" + "</li>";
		line += "</ul><br style=\"clear:left\"/></div></alert>";

		getWriter(response).append("<form method='POST' action='/j_security_check'>" + line + "</form>");
		htmlHeaderWriter.writeFooter(response);
	}
}