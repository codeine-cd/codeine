package yami.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import yami.YamiVersion;
import yami.configuration.ConfigurationManager;
import yami.configuration.GlobalConfiguration;
import yami.model.Constants;
import yami.model.DataStore;
import yami.model.DataStoreRetriever;

public class AggregateNodesServlet extends HttpServlet
{
	private static final Logger log = Logger.getLogger(AggregateNodesServlet.class);
	private static final long serialVersionUID = 1L;
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		log.debug("aggregate request");
		ConfigurationManager cm = ConfigurationManager.getInstance();
		GlobalConfiguration gc = cm.getCurrentGlobalConfiguration();
		String hostname = gc.server_dns_name != null ? gc.server_dns_name : InetAddress.getLocalHost().getCanonicalHostName();
		DataStore ds = getDataStore();
		PrintWriter writer = res.getWriter();
		writer.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
		writer.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		writer.println("<head>");
		writer.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
		// writer.println("<meta http-equiv=\"refresh\" content=\"5\" />");
		writer.println("<title>yami aggregate - " + cm.getConfiguredProject().name + "</title>");
		writer.println("<link rel=\"stylesheet\" href=\"../style.css\" type=\"text/css\" />");
		writer.println("<script src=\"../dashboard.js\" type=\"text/javascript\" ></script>");
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
		writer.println("          <li class=\"last\"><a href=\"http://" + hostname + ":" + gc.getServerPort() + Constants.AGGREGATE_NODE_CONTEXT + "\">Aggregate</a></li>");
		writer.println("          <li class=\"last\"><a href=\"http://" + hostname + ":" + gc.getServerPort() + Constants.PEERS_DASHBOARD_CONTEXT + "\">Peers</a></li>");
//		writer.println("          <li class=\"last\"><a href=\"http://" + hostname + ":" + gc.getServerPort() + "/nodes" + "\">Nodes</a></li>");
		writer.println("        </ul>");
		writer.println("    </div>");
		writer.println("    <div id=\"body\">");
		writer.println("    <div id=\"content\">");
		NodeAggregator aggregator = new NodeAggregator();
		for (VersionItem item : aggregator.aggregate().values())
		{
			// start building monitored instance line:
			String line = "            <alert><div class=\"alertbar\"><ul>";
			String version = item.version();
			String className = "g";
			String versionClass = "version" + className ;
			if (null != version)
			{
			    line += "<li><a class=\"" + versionClass + "\" href=\"none\">"+version+"</a></li>";
			    line += "<li><a class=\"" + versionClass + "\" href=\"none\">"+item.count()+"</a></li>";
			}
			if (gc.isSwitchVersionEnabled())
			{
			    String link = "none";//node.peer.getPeerSwitchVersionLink(node.name, "");
			    line += "<li>" +
			    		"<input class=\"version\" type=\"text\" id=\""+version+"_input\" >"+item.count()+"</input>" +
			    		"</li>" + "<li>" +
			    		"<button class=\"command\" onClick=\"viewDashboard('"+version+"')\">View</button>" +
			    		"</li>";
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

//	private String getVersion(DataStore ds, Node node) 
//	{
//	    CollectorOnNodeState result = ds.getResult(node, new VersionCollector());
//	    if (null == result)
//	    {
//		return null;
//	    }
//	    return result.getLast().output;
//	}
//	
//	private String getLink(HttpCollector collector, Node node)
//	{
//		return Constants.CLIENT_LINK.replace(Constants.PEER_NAME, node.peer.dnsName()).
//			replace(Constants.NODE_NAME, node.name).
//			replace(Constants.COLLECTOR_NAME, collector.name).
//			replace(Constants.CLIENT_PORT, ConfigurationManager.getInstance().getCurrentGlobalConfiguration().getClientPort() + "");
//	}
	
	private DataStore getDataStore()
	{
		return DataStoreRetriever.getD();
	}
	
}
