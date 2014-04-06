package codeine.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractApiServlet extends AbstractServlet{

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		setNoCache(response);
		super.doGet(request, response);
	}

	private void allowCrossSite(HttpServletRequest request, HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		if (request.getHeader("Access-Control-Request-Method") != null && "OPTIONS".equals(request.getMethod())) {
		// CORS "pre-flight" request
		response.addHeader("Access-Control-Allow-Methods","GET, POST, PUT, DELETE");
		response.addHeader("Access-Control-Allow-Headers",
		"X-Requested-With,Origin,Content-Type, Accept");
		}
	}
	
	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		allowCrossSite(request, response);
		super.service(request, response);
	}
}
