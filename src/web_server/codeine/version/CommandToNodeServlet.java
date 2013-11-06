package codeine.version;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.servlet.AbstractServlet;
import codeine.servlet.HtmlHeaderWriter;
import codeine.servlets.RawOutputServlet;
import codeine.utils.network.HttpUtils;

import com.google.common.base.Function;
import com.google.inject.Inject;

public class CommandToNodeServlet extends AbstractServlet {
	private static final Logger log = Logger.getLogger(RawOutputServlet.class);
	private static final long serialVersionUID = 1L;

	@Inject private HtmlHeaderWriter htmlHeaderWriter;
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		log.debug("myGet request");
		String projectName = request.getParameter("project");
		String link = request.getParameter("link");
		String command = request.getParameter("command");
		final PrintWriter writer = getWriter(response);
		htmlHeaderWriter.writeHeader(request, response, projectName, projectName + " / command / " + command);
		writer.println("<pre>");
		Function<String, Void> function = new Function<String, Void>(){
			@Override
			public Void apply(String input){
				writer.println(input);
				writer.flush();
				return null;
			}
		};
		HttpUtils.doGET(link, function);
		writer.println("</pre>");
		htmlHeaderWriter.writeFooter(response);
	}

}
