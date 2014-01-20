package codeine.servlets;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.configuration.PathHelper;
import codeine.model.Constants;
import codeine.servlet.AbstractServlet;
import codeine.utils.FilesUtils;
import codeine.utils.TextFileUtils;

import com.google.inject.Inject;

public class FileGetterServlet extends AbstractServlet {
	private static final Logger log = Logger.getLogger(FileGetterServlet.class);
	private static final long serialVersionUID = 1L;

	@Inject	private PathHelper pathHelper;

	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		log.debug("RawOutputServlet request");
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		String path = request.getParameter("path");
		String line = request.getParameter("line");
		int fromLine = Integer.valueOf(line);
		String dirNameFull = pathHelper.getPluginsOutputDir(projectName) + "/" + path;
		String file = dirNameFull + "/log";
		List<String> lines = TextFileUtils.getContentFromLine(file, fromLine);
		FileContentJson j = new FileContentJson(lines, FilesUtils.exists(dirNameFull + Constants.COMMAND_FINISH_FILE));
		PrintWriter writer = getWriter(response);
		writer.write(gson().toJson(j));
	}
	
	public static class FileContentJson {
		public boolean eof;
		public List<String> lines;
		public FileContentJson(List<String> lines, boolean eof) {
			super();
			this.eof = eof;
			this.lines = lines;
		}
		
	}

	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return canReadProject(request);
	}

}
