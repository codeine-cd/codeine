package codeine.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;

import codeine.exceptions.UnAuthorizedException;
import codeine.utils.ServletUtils;

import com.google.gson.Gson;

public abstract class AbstractServlet extends HttpServlet{

	private static final Logger log = Logger.getLogger(AbstractServlet.class);
	private static final long serialVersionUID = 1L;
	
	@Inject private Gson gson;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			if (!checkPermissions(request)) {
				throw new UnAuthorizedException();
			}
			myGet(request, response);
		} catch (Exception e) {
			handleError(e, response);
		}
	}
	
	
	
	@Override
	protected final void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			if (!checkPermissions(request)) {
				throw new UnAuthorizedException();
			}
			myPost(request, response);
		} catch (Exception e) {
			handleError(e, response);
		}
	}
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			if (!checkPermissions(request)) {
				throw new UnAuthorizedException();
			}
			myDelete(request, response);
		} catch (Exception e) {
			handleError(e, response);
		}
	}

	protected boolean checkPermissions(HttpServletRequest request) {
		return true;
	}
	
	protected void myDelete(HttpServletRequest request, HttpServletResponse response) {
		writeNotFound(request, response);
	}

	protected void handleError(Exception e, HttpServletResponse response) {
		log.warn("Error in servlet", e);
		if (e instanceof UnAuthorizedException) {
			response.setStatus(HttpStatus.UNAUTHORIZED_401);
			getWriter(response).write("UNAUTHORIZED Request, please provide API Token");
		} else {
			getWriter(response).write("Internak Server Error: \n");
			e.printStackTrace(getWriter(response));
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
		}
	}
	
	protected void writeNotFound(HttpServletRequest request, HttpServletResponse response) {
		response.setStatus(HttpStatus.METHOD_NOT_ALLOWED_405);
		PrintWriter writer = getWriter(response);
		writer.write("Codeine dosen't support this action");
	}
	
	protected void myPost(HttpServletRequest request, HttpServletResponse response) {
		writeNotFound(request, response);
	}

	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		writeNotFound(request, response);
	}

	protected final PrintWriter getWriter(HttpServletResponse response) {
		return ServletUtils.getWriter(response);
	}

	protected Gson gson(){
		return gson;
	}

	protected final <T> T readBodyJson(HttpServletRequest request, Class<T> clazz) {
		return gson().fromJson(readBody(request), clazz);
	}
	protected final void writeResponseJson(HttpServletResponse response, Object json) {
		getWriter(response).write(gson().toJson(json));
	}

	protected String readBody(HttpServletRequest request) {
		String post = null;
		try {
			StringBuilder status = new StringBuilder();
			BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));
			String inputLine;
	
			while ((inputLine = in.readLine()) != null) {
				status.append(inputLine);
			}
			in.close();
			post = status.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return post;
	}
}
