package codeine.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.HashMap;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;

import codeine.model.Constants;
import codeine.utils.ExceptionUtils;
import codeine.utils.ServletUtils;
import codeine.utils.TextFileUtils;
import codeine.utils.exceptions.FileReadWriteException;
import codeine.utils.exceptions.InShutdownException;
import codeine.utils.exceptions.ProjectNotFoundException;
import codeine.utils.exceptions.UnAuthorizedException;

import com.google.common.collect.Maps;
import com.google.common.net.HttpHeaders;
import com.google.gson.Gson;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

public abstract class AbstractServlet extends HttpServlet{

	private static final Logger log = Logger.getLogger(AbstractServlet.class);
	private static final long serialVersionUID = 1L;
	
	@Inject private Gson gson;
	private @Inject PermissionsManager permissionsManager;
	
	// TODO - Add final after removing front end servlets
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
	protected final void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			if (!checkPermissions(request)) {
				throw new UnAuthorizedException();
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
				throw new UnAuthorizedException();
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
				throw new UnAuthorizedException();
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
		
		if (e instanceof UnAuthorizedException) {
			response.setStatus(HttpStatus.UNAUTHORIZED_401);
			getWriter(response).write("UNAUTHORIZED Request, please provide API Token");
		} else if (e instanceof IllegalArgumentException){
			getWriter(response).write("Bad request - please check api help\n");
			response.setStatus(HttpStatus.BAD_REQUEST_400);
		} else if (e instanceof InShutdownException){
			getWriter(response).write("Cannot execute - preparing for shutdown\n");
			response.setStatus(HttpStatus.FORBIDDEN_403);
		} else {
			getWriter(response).write("Internal Server Error: \n");
			e.printStackTrace(getWriter(response));
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
		}
	}
	protected void handleErrorRequestFromBrowser(Exception e, HttpServletResponse response) {
		log.warn("Error in servlet", e);
		HashMap<String, String> dic = Maps.newHashMap();
		String contents;
		String message;
		String title;
		if (e instanceof ProjectNotFoundException) {
			response.setStatus(HttpStatus.NOT_FOUND_404);
			contents = TextFileUtils.getContents(Constants.getResourcesDir() + "/html/generalError.html");
			message = e.getMessage();
			title = "Error 404";
			
		} else if  (e instanceof UnAuthorizedException) {
			response.setStatus(HttpStatus.UNAUTHORIZED_401);
			contents = TextFileUtils.getContents(Constants.getResourcesDir() + "/html/generalError.html");
			message = "You are not authorized to access this page";
			title = "Error 401";
		} else if  (e instanceof FileReadWriteException) {
			contents = TextFileUtils.getContents(Constants.getResourcesDir() + "/html/500.html");
			message = e.getMessage(); 
			dic.put("stack_trace", ExceptionUtils.getStackTrace(e)); 
			title = "Error 500";
		} else {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
			contents = TextFileUtils.getContents(Constants.getResourcesDir() + "/html/500.html");
			message = ExceptionUtils.getRootCauseMessage(e) == null ? "No Message was Provided" : ExceptionUtils.getRootCauseMessage(e); 
			dic.put("stack_trace", ExceptionUtils.getStackTrace(e)); 
			title = "Error 500";
		}
		Template template = Mustache.compiler().escapeHTML(false).compile(contents);
		dic.put("message", message);		
		dic.put("title", title);		
		getWriter(response).write(template.execute(dic));
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
		return permissionsManager.canRead(projectName, request);
	}
	protected final boolean canCommandProject(HttpServletRequest request) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		return permissionsManager.canCommand(projectName, request);
	}
	protected final boolean canConfigureProject(HttpServletRequest request) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		return permissionsManager.canConfigure(projectName, request);
	}
	protected final boolean isAdministrator(HttpServletRequest request) {
		return permissionsManager.isAdministrator(request);
	}
	
	protected final String projectName(HttpServletRequest request) {
		return request.getParameter(Constants.UrlParameters.PROJECT_NAME);
	}
	
	protected final void setNoCache(HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
		response.setDateHeader("Expires", 0); // Proxies.
	}
}
