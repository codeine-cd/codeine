package codeine;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class InvalidRequestServlet extends HttpServlet{

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(InvalidRequestServlet.class);
    
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    	response.sendRedirect("/resources/html/404.html");
    }
}
