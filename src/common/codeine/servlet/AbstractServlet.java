package codeine.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Type;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;

import codeine.model.Constants;
import codeine.permissions.UserPermissionsGetter;
import codeine.utils.ExceptionUtils;
import codeine.utils.ServletUtils;
import codeine.utils.exceptions.InShutdownException;
import codeine.utils.exceptions.UnAuthorizedException;

import com.google.common.net.HttpHeaders;
import com.google.gson.Gson;

public abstract class AbstractServlet extends HttpServlet{

	private static final Logger log = Logger.getLogger(AbstractServlet.class);
	private static final long serialVersionUID = 1L;
	
	@Inject private Gson gson;
	private @Inject UserPermissionsGetter permissionsManager;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			if (!checkPermissions(request)) {
				throw newUnauthrizedException(request);
			}
			myGet(request, response);
		} catch (Exception e) {
			handleError(e, response);
		}
	}

	private UnAuthorizedException newUnauthrizedException(HttpServletRequest request) {
		return new UnAuthorizedException(permissionsManager.user(request) + " not authorized for url " + request.getRequestURI());
	}

	@Override
	protected final void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			if (!checkPermissions(request)) {
				throw newUnauthrizedException(request);
			}
			myPut(request, response);
		} catch (Exception e) {
			handleError(e, response);
		}
	}
	
	
	@Override
	protected final void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			if (!checkPermissions(request)) {
				throw newUnauthrizedException(request);
			}
			myPost(request, response);
		} catch (Exception e) {
			handleError(e, response);
		}
	}
	
	@Override
	protected final void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			if (!checkPermissions(request)) {
				throw newUnauthrizedException(request);
			}
			myDelete(request, response);
		} catch (Exception e) {
			handleError(e, response);
		}
	}

	protected abstract boolean checkPermissions(HttpServletRequest request) ;
	
	protected void myDelete(HttpServletRequest request, HttpServletResponse response) {
		writeNotFound(request, response);
	}

	protected void myPut(HttpServletRequest request, HttpServletResponse response) {
		writeNotFound(request, response);
	}
	
	protected void handleError(Exception e, HttpServletResponse response) {
		log.warn("Error in servlet", e);
		ApiError error;
		if (e instanceof UnAuthorizedException) {
			response.setStatus(HttpStatus.UNAUTHORIZED_401);
			error = new ApiError("UNAUTHORIZED Request","Please provide API Token",e.getMessage());
			//getWriter(response).write("UNAUTHORIZED Request, please provide API Token");
		} else if (e instanceof IllegalArgumentException){
			error = new ApiError("Bad request","Please check api help",e.getMessage());
			response.setStatus(HttpStatus.BAD_REQUEST_400);
		} else if (e instanceof InShutdownException){
			error = new ApiError("Cannot execute","Preparing for shutdown",e.getMessage());
			response.setStatus(HttpStatus.FORBIDDEN_403);
		} else {
			error = new ApiError("Internal Server Error",e.getMessage() ,e.getMessage());
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
		}
		getWriter(response).write(gson().toJson(error));
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
	
	protected final <T> T readBodyJson(HttpServletRequest request, Type listType) {
		return gson().fromJson(readBody(request), listType);
	}
	
	protected final void writeResponseJson(HttpServletResponse response, Object json) {
		getWriter(response).write(gson().toJson(json));
	}

	protected final void writeResponseGzipJson(HttpServletResponse response, Object json) {
		try {
			try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(getGzipStream(response),
					response.getCharacterEncoding()))) {
				writer.write(gson().toJson(json));
			}
		} catch (IOException e) {
			throw ExceptionUtils.asUnchecked(e);
		}
	}

	private ServletResponseGZIPOutputStream getGzipStream(HttpServletResponse response) throws IOException {
		response.addHeader(HttpHeaders.CONTENT_ENCODING, "gzip");
		return new ServletResponseGZIPOutputStream(response.getOutputStream());
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
	
	protected final boolean canReadProject(HttpServletRequest request) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		return permissionsManager.user(request).canRead(projectName);
	}
	protected final boolean canCommandProject(HttpServletRequest request) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		return permissionsManager.user(request).canCommand(projectName);
	}
	protected final boolean canConfigureProject(HttpServletRequest request) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		return permissionsManager.user(request).canConfigure(projectName);
	}
	protected final boolean isAdministrator(HttpServletRequest request) {
		return permissionsManager.user(request).isAdministrator();
	}
	
	protected final String projectName(HttpServletRequest request) {
		return request.getParameter(Constants.UrlParameters.PROJECT_NAME);
	}
	
	public static final void setNoCache(HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
		response.setDateHeader("Expires", 0); // Proxies.
	}
}
