package simple_server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.servlet.DefaultServlet;

final class LoginServlet2 extends DefaultServlet {
	
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		response.getWriter().append(
				"<html><body><form method='POST' action='/j_security_check'>" 
		+ "<table>" 
						+ "<tr>"
						+ "<td>"
						+ "<label>User:</label>"
						+ "</td>"
						+ "<td>"
						+ "<input type='text' name='j_username'/>" 
						+ "</td>"
						+ "</tr>"
						+ "<tr>"
						+ "<td>"
						+ "<label>Password:</label>"
						+ "</td>"
						+ "<td>"
						+ "<input type='password' name='j_password'/>" 
						+ "</td>"
						+ "</tr>"
						+ "<tr>"
						+ "<td>"
						+ "</td>"
						+ "<td>"
						+ "<input type='submit' value='Login'/>" 
						+ "</td>"
						+ "</tr>"
						+ "</table>" 
						+ "</form></body></html>");
	}
}