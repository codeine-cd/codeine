package codeine.servlet;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeine.jsons.info.CodeineRuntimeInfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;

public class CodeineInfoServlet extends AbstractApiServlet {

	private static final long serialVersionUID = 1L;
	
	@Inject private CodeineRuntimeInfo codeineRuntimeInfo;
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = getWriter(response);
		writer.print(gson.toJson(codeineRuntimeInfo));
	}

	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return true;
	}

}
