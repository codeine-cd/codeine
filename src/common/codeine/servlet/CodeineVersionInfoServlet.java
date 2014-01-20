package codeine.servlet;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeine.jsons.info.CodeineRuntimeInfo;

import com.google.inject.Inject;

public class CodeineVersionInfoServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	
	@Inject private CodeineRuntimeInfo codeineRuntimeInfo;
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = getWriter(response);
		writer.print(codeineRuntimeInfo.version());
	}

	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return true;
	}
}
