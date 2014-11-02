package simple_server;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.model.Constants;
import codeine.servlet.AbstractServlet;

public class WelcomeServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(WelcomeServlet.class);

	@Override
	protected void myPost(HttpServletRequest req, HttpServletResponse resp){
		log.info("in post");
	}
	
	@Override
	protected void myGet(HttpServletRequest req, HttpServletResponse resp){
		PrintWriter writer = getWriter(resp);
		getParameter(req, "ParamId");
		writer.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
		writer.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		writer.println("<head>");
		writer.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
		writer.println("<script src=\".." + Constants.RESOURCESS_CONTEXT
				+ "/jquery-1.10.2.js\" type=\"text/javascript\" ></script>");
		writer.println("<script src=\".." + Constants.RESOURCESS_CONTEXT
				+ "/auth.js\" type=\"text/javascript\" ></script>");
		writer.println("</head>");
		writer.println("<body>");
		writer.println("</body>");
		writer.println("</html>");
	}

	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return false;
	}
} 
