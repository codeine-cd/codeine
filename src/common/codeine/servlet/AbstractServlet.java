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

import codeine.utils.ServletUtils;

import com.google.gson.Gson;

public abstract class AbstractServlet extends HttpServlet{

	private static final Logger log = Logger.getLogger(AbstractServlet.class);
	private static final long serialVersionUID = 1L;
	
	@Inject private Gson gson;
	
	@Override
	protected final void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
//			log.info("user is " + request.getUserPrincipal().getName());
			myGet(request, response);
		} catch (Exception e) {
			handleError(e, response);
		}
	}

	@Override
	protected final void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			myPost(request, response);
		} catch (Exception e) {
			handleError(e, response);
		}
	}
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			myDelete(request, response);
		} catch (Exception e) {
			handleError(e, response);
		}
	}

	private void handleError(Exception e, HttpServletResponse response) {
		log.warn("error in servlet", e);
		getWriter(response).write("error in servlet\n");
		e.printStackTrace(getWriter(response));
		response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
	}
	
	protected void myDelete(HttpServletRequest request, HttpServletResponse response) {
		writeNotFound(request, response);
	}

	private void writeNotFound(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = getWriter(response);
		writer.write("Oooooooooooooops...");
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
