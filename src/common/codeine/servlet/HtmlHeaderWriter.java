package codeine.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeine.CodeineVersion;
import codeine.jsons.global.GlobalConfigurationJson;
import codeine.model.Constants;
import codeine.utils.StringUtils;
import codeine.utils.network.HttpUtils;

public class HtmlHeaderWriter {
	
	@Inject private GlobalConfigurationJson globalConfiguration;
	@Inject private PermissionsManager permissionsManager;
	
	public void writeHeader(HttpServletRequest request, HttpServletResponse response, String projectName, String path) {
		PrintWriter writer = getWriter(response);
		String pathLine = "codeine / " + path;
		String prefix = hasProjectContext(projectName) ? projectName + " - " : "";

		writer.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
		writer.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		writer.println("<head>");
		writer.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
		// writer.println("<meta http-equiv=\"refresh\" content=\"5\" />");
		writer.println("<title>" + prefix + "codeine</title>");
		writer.println("<link rel=\"stylesheet\" href=\"" + Constants.RESOURCESS_CONTEXT + "/css/style.css\" type=\"text/css\" />");
		writer.println("<link rel=\"stylesheet\" href=\"" + Constants.RESOURCESS_CONTEXT + "/jquery-ui-1.10.3.custom/css/ui-lightness/jquery-ui-1.10.3.custom.css\" type=\"text/css\" />");
		writer.println("<script src=\"" + Constants.RESOURCESS_CONTEXT + "/jquery-ui-1.10.3.custom/js/jquery-1.9.1.js\" type=\"text/javascript\" ></script>");
		writer.println("<script src=\"" + Constants.RESOURCESS_CONTEXT + "/jquery-ui-1.10.3.custom/js/jquery-ui-1.10.3.custom.js\" type=\"text/javascript\" ></script>");
		writer.println("<script src=\"" + Constants.RESOURCESS_CONTEXT + "/js/libs/metawidget-core.min.js\" type=\"text/javascript\" ></script>");
		writer.println("<script src=\"" + Constants.RESOURCESS_CONTEXT + "/js/dashboard.js\" type=\"text/javascript\" ></script>");
		writer.println("</head>");
		writer.println("<body>");
		writer.println("<div id='dialog-confirm'>");
		writer.println("</div>");
		writer.println("<input type='hidden' id='projectName' value='"+projectName+"'>");
		writer.println("<div id=\"container\">");
		writer.println("  <div id=\"header\">");
		String user = permissionsManager.user(request);
		String returnUrl = request.getRequestURL().toString();
		if (!StringUtils.isEmpty(request.getQueryString())){
			returnUrl += '?' + request.getQueryString();
		}
		if (null != user){
			writer.println("<label>"+user+"</label><label> | </label>"+"<a href=\"/logout?from="+HttpUtils.encode(returnUrl)+"\">logout</a>");
		} else {
			String mailto = "<a href=\"/register"+addProjectParamFirst(projectName)+"\">sign up</a>";
			writer.println(mailto + "<label> | </label><a href=\"/login?from="+HttpUtils.encode(returnUrl)+addProjectParam(projectName)+"\">login</a>");
		}
		writer.println("      <h1><a href=\"/\"><img src='/resources/img/codeine_50x50.png' alt='codeine' />codeine</a></h1>");
		writer.println("        <h2>beta</h2>");
		writer.println("        <h2>" + CodeineVersion.get() + "</h2>");
		writer.println("        <div class=\"clear\"></div>");
		writer.println("    </div>");
		writer.println("    <div class=\"path\" ><h3>" + pathLine + "</h3> </div>");
		writer.println("    <div id=\"nav\">");
		writer.println("      <ul>");
		if (permissionsManager.isModifiable("codeine", request)){
			writer.println("          <li class=\"start\"><a href=\"" + Constants.MANAGEMENT_CONTEXT + "\">Manage Codeine</a></li>");
		}
		writer.println("          <li class=\"start\"><a href=\"" + Constants.PROJECTS_DASHBOARD_CONTEXT + "\">Projects</a></li>");
		if (hasProjectContext(projectName)) {
			String projectParams = "?" + getProjectParam(projectName);
			writer.println("          <li class=\"start\"><a href=\"" + Constants.DASHBOARD_CONTEXT + projectParams + "\">Dashboard</a></li>");
			writer.println("          <li class=\"start\"><a href=\"" + Constants.DASHBOARD_CONTEXT + projectParams + "&alerts=true" + "\">Alerts</a></li>");
			writer.println("          <li class=\"start\"><a href=\"" + Constants.AGGREGATE_NODE_CONTEXT + projectParams + "\">Aggregate</a></li>");
			writer.println("          <li class=\"last\"><a href=\"" + Constants.LABELS_CONTEXT + projectParams + "\">Labels</a></li>");
			writer.println("          <li class=\"last\"><a href=\"" + Constants.COMMANDS_LOG_CONTEXT + projectParams + "\">Commands Logs</a></li>");
		}
		writer.println("        </ul>");
		writer.println("    </div>");
		writer.println("    <div id=\"body\">");
		writer.println("    <div id=\"content\">");
		writer.flush();
	}

	private String getProjectParam(String projectName) {
		return "project=" + HttpUtils.encode(projectName);
	}

	private String addProjectParam(String projectName) {
		return (hasProjectContext(projectName)) ?
			"&" + getProjectParam(projectName) : "";
	}
	private String addProjectParamFirst(String projectName) {
		return (hasProjectContext(projectName)) ?
				"?" + getProjectParam(projectName) : "";
	}

	private PrintWriter getWriter(HttpServletResponse response) {
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return writer;
	}

	private boolean hasProjectContext(String projectName) {
		return null != projectName;
	}

	public void writeFooter(HttpServletResponse response) {
		PrintWriter writer = getWriter(response);
		writer.println("        </div>        ");
		writer.println("    <div class=\"clear\"></div>");
		writer.println("    </div>");
		writer.println("    <div id='footer'>");
		writer.println("    	<div class='footer-content'>");
		writer.println("    	</div>");
		writer.println("    </div>");
		writer.println("    </div>");
		writer.println("</div>");
		writer.println("</body>");
		writer.println("</html>");
		writer.close();
	}
}
