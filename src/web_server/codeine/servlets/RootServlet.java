package codeine.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeine.model.Constants;
import codeine.servlet.AbstractServlet;

public class RootServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		AbstractServlet.setNoCache(response);
		response.sendRedirect(Constants.ANGULAR_CONTEXT);
	}
	
}