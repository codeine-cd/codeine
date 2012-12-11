package yami;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.*;

import yami.configuration.*;
import yami.mail.*;
import yami.model.*;

public class DashboardServlet extends HttpServlet
{
	private static final Logger log = Logger.getLogger(DashboardServlet.class);
	private static final long serialVersionUID = 1L;
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		log.debug("dashboard request");
		String hostname = java.net.InetAddress.getLocalHost().getCanonicalHostName();
		GlobalConfiguration gc = ConfigurationManager.getInstance().getCurrentGlobalConfiguration();
		DataStore ds = getDataStore();
		PrintWriter writer = res.getWriter();
		writer.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
		writer.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		writer.println("<head>");
		writer.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
		// writer.println("<meta http-equiv=\"refresh\" content=\"5\" />");
		writer.println("<title>YAMI Dashboard</title>");
		writer.println("<link rel=\"stylesheet\" href=\"../style.css\" type=\"text/css\" />");
		writer.println("");
		writer.println("</head>");
		writer.println("<body>");
		writer.println("<div id=\"container\">");
		writer.println("  <div id=\"header\">");
		writer.println("      <h1><a href=\"/\">yami</a></h1>");
		writer.println("        <h2>" + YamiVersion.get() + "</h2>");
		writer.println("        <div class=\"clear\"></div>");
		writer.println("    </div>");
		writer.println("    <div id=\"nav\">");
		writer.println("      <ul>");
		writer.println("          <li class=\"start\"><a href=\"http://" + hostname + ":" + gc.getServerPort() + Constants.DASHBOARD_CONTEXT + "\">Dashboard</a></li>");
		writer.println("          <li class=\"last\"><a href=\"http://" + hostname + ":" + gc.getServerPort() + "/server" + "\">Server</a></li>");
		writer.println("          <li class=\"last\"><a href=\"http://" + hostname + ":" + gc.getServerPort() + "/peers" + "\">Peers</a></li>");
		writer.println("          <li class=\"last\"><a href=\"http://" + hostname + ":" + gc.getServerPort() + "/nodes" + "\">Nodes</a></li>");
		writer.println("        </ul>");
		writer.println("    </div>");
		writer.println("    <div id=\"body\">");
		writer.println("    <div id=\"content\">");
		writer.println("      <h2>Active Monitors</h2>");
		for (Node node : ds.appInstances())
		{
			boolean fail = false;
			for (HttpCollector collector : ds.collectors())
			{
				CollectorOnAppState result = ds.getResult(node, collector);
				if (result == null || !result.state())
				{
					fail = true;
					break;
				}
			}
			// start building monitored instance line:
			String line = "            <alert><div class=\"alertbar\"><ul>";
			String goodbad = fail ? "nameb" : "nameg";
			line += "<li><a class=\"" + goodbad + "\" href=\"" + node.getLogLink() + "\">" + node.nick() + "</a></li>";
			// build result buttons for each collector:
			for (HttpCollector collector : ds.collectors())
			{
				CollectorOnAppState result = ds.getResult(node, collector);
				// in case no result found:
				if (null == result)
				{
					line += "<li><a class=\"na\" title=\"" + collector.name + "\" href=\"" + getLink(collector, node) + "\">?</a></li>";
				}
				else
				{
					// change css class depending on result:
					goodbad = result.state() ? "good" : "bad";
					line += "<li><a class=\"" + goodbad + "\" title=\"" + collector.name + "\" href=\"" + getLink(collector, node) + "\">?</a></li>";
				}
			}
			line += "</ul><br style=\"clear:left\"/></div></alert>";
			writer.println(line);
		}
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
	
	private String getLink(HttpCollector collector, Node node)
	{
		return Constants.CLIENT_LINK.replace(Constants.NODE_NAME, node.node.name).replace(Constants.APP_NAME, node.name).replace(Constants.COLLECTOR_NAME, collector.name);
	}
	
	private DataStore getDataStore()
	{
		return DataStoreRetriever.getD();
	}
	
}
