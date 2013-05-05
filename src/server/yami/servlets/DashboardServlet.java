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
import yami.configuration.Command;
import yami.configuration.ConfigurationManager;
import yami.configuration.GlobalConfiguration;
import yami.configuration.HttpCollector;
import yami.configuration.KeepaliveCollector;
import yami.configuration.Node;
import yami.configuration.VersionCollector;
import yami.mail.CollectorOnNodeState;
import yami.model.Constants;
import yami.model.DataStore;
import yami.model.DataStoreRetriever;

public class DashboardServlet extends HttpServlet
{
	private static final Logger log = Logger.getLogger(DashboardServlet.class);
	private static final long serialVersionUID = 1L;
	private String m_version;
	private int m_max;
	private int m_count;
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		log.debug("dashboard request");
		m_version = req.getParameter("version");
		String countString = req.getParameter("count");
		m_max = Integer.MAX_VALUE;
		if (null != countString)
		{
			m_max = Integer.valueOf(countString);
		}
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
		writer.println("<title>yami dashboard - " + cm.getConfiguredProject().name + "</title>");
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
		writer.println("          <li class=\"start\"><a href=\"http://" + hostname + ":" + gc.getServerPort() + Constants.DASHBOARD_CONTEXT
				+ "\">Dashboard</a></li>");
		writer.println("          <li class=\"last\"><a href=\"http://" + hostname + ":" + gc.getServerPort() + Constants.AGGREGATE_NODE_CONTEXT
				+ "\">Aggregate</a></li>");
		writer.println("          <li class=\"last\"><a href=\"http://" + hostname + ":" + gc.getServerPort() + Constants.PEERS_DASHBOARD_CONTEXT
				+ "\">Peers</a></li>");
		// writer.println("          <li class=\"last\"><a href=\"http://" + hostname + ":" + gc.getServerPort() +
		// "/nodes" + "\">Nodes</a></li>");
		writer.println("        </ul>");
		writer.println("    </div>");
		writer.println("    <div id=\"body\">");
		writer.println("    <div id=\"content\">");
		writer.println("<alert><div class=\"alertbar\">");
		writer.println("<button class=\"command\" onClick=\"switchVersionToCheckedItems()\">Switch version to selected nodes</button>");
		writer.println("<input class=\"version\" type=\"text\" id=\"newVersionAll\" />");
		writer.println("</div></alert>");
		for (Node node : ds.appInstances())
		{
			String version = getVersion(ds, node);
			if (filterByVersion(version))
			{
				continue;
			}
			Node internalNode = node.peer.internalNode();
			CollectorOnNodeState keepaliveResult = ds.getResult(internalNode, new KeepaliveCollector());
			boolean fail = false;
			for (HttpCollector collector : ds.collectors())
			{
				CollectorOnNodeState result = ds.getResult(node, collector);
				if (result == null || !result.state())
				{
					fail = true;
					break;
				}
			}
			// start building monitored instance line:
			String line = "            <alert><div class=\"alertbar\"><ul>";
			String className = fail ? "b" : "g";
			line += "<li><a class=\"name" + className + "\" href=\"" + node.getLogLink() + "\">" + node.nick() + "</a></li>";
			// build result buttons for each collector:
			for (HttpCollector collector : ds.collectors())
			{
				CollectorOnNodeState result = ds.getResult(node, collector);
				log.debug(collector + " result for " + node + " is: " + result);
				if (node.disabled())
				{
					line += "<li><a class=\"na\" title=\"node is disabled\" href=\"na\">?</a></li>";
				}
				else if (!keepaliveResult.state())
				{
					line += "<li><a class=\"na\" title=\"keepalive is dead\" href=\"" + getLink(collector, node) + "\">?</a></li>";
				}
				else if (null == result)
				{
					line += "<li><a class=\"na\" title=\"" + collector.name + "\" href=\"" + getLink(collector, node) + "\">?</a></li>";
				}
				else
				{
					// change css class depending on result:
					String goodbad = result.state() ? "good" : "bad";
					line += "<li><a class=\"" + goodbad + "\" title=\"" + collector.name + "\" href=\"" + getLink(collector, node) + "\">?</a></li>";
				}
			}
			String versionClass = "version" + className;
			if (null != version)
			{
				line += "<li><a class=\"" + versionClass + "\" href=\"none\">" + version + "</a></li>";
			}
			if (gc.isSwitchVersionEnabled())
			{
				String link = node.peer.getPeerSwitchVersionLink(node.name, "");
				line += "<li>" + "<input class=\"version\" type=\"text\" id=\"" + node.name + "_newVersion\" />" + "</li>" + "<li>"
						+ "<button class=\"command\" onClick=\"switchVersion('" + node.name + "','" + link + "')\">Switch-Version</button>" + "</li>";
			}
			for (Command command : cm.getConfiguredProject().command)
			{
				String link = node.peer.getPeerCommandLink(node.name, command.name);
				line += "<li>" + "<button class=\"command\" onClick=\"commandNode('" + node.name + "','" + command.title() + "','" + link + "')\">"
						+ command.title() + "</button>" + "</li>";
			}
			line += "<li><input class=\"checkbox\" type=\"checkbox\" id=\"checkbox_" + node.name + "\"/></li>";
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
	
	private boolean filterByVersion(String version)
	{
		if (null == m_version)
		{
			return false;
		}
		if (!m_version.equals(version))
		{
			return true;
		}
		if (m_count >= m_max)
		{
			return true;
		}
		m_count++;
		return false;
	}
	
	private String getVersion(DataStore ds, Node node)
	{
		CollectorOnNodeState result = ds.getResult(node, new VersionCollector());
		if (null == result)
		{
			return null;
		}
		return result.getLast().output;
	}
	
	private String getLink(HttpCollector collector, Node node)
	{
		return Constants.CLIENT_LINK.replace(Constants.PEER_NAME, node.peer.dnsName()).replace(Constants.NODE_NAME, node.name)
				.replace(Constants.COLLECTOR_NAME, collector.name)
				.replace(Constants.CLIENT_PORT, ConfigurationManager.getInstance().getCurrentGlobalConfiguration().getClientPort() + "");
	}
	
	private DataStore getDataStore()
	{
		return DataStoreRetriever.getD();
	}
	
}
