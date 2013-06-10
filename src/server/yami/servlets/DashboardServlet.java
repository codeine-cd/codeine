package yami.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

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
import yami.model.VersionResult;

import com.google.inject.Inject;

public class DashboardServlet extends HttpServlet
{
	private static final Logger log = Logger.getLogger(DashboardServlet.class);
	private static final long serialVersionUID = 1L;
	private final ConfigurationManager configurationManager;
	
	@Inject
	public DashboardServlet(ConfigurationManager configurationManager)
	{
		super();
		this.configurationManager = configurationManager;
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		try
		{
			showDashboard(req, res);
		}
		catch (Exception ex)
		{
			log.warn("excption ", ex);
		}
	}
	public void showDashboard(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		String paramVersion;
		int paramMax;
		log.debug("dashboard request");
		paramVersion = req.getParameter("version");
		String countString = req.getParameter("count");
		paramMax = Integer.MAX_VALUE;
		if (null != countString)
		{
			paramMax = Integer.valueOf(countString);
		}
		boolean alerts = Boolean.parseBoolean(req.getParameter("alerts") == null ? "false" : req.getParameter("alerts"));
		boolean readOnly = Boolean.parseBoolean(req.getParameter("readonly") == null ? "true" : req.getParameter("readonly"));
		
		VersionFilter versionFilter = new VersionFilter(paramVersion, paramMax);
		GlobalConfiguration gc = configurationManager.getCurrentGlobalConfiguration();
		String hostname = gc.server_dns_name != null ? gc.server_dns_name : InetAddress.getLocalHost().getCanonicalHostName();
		DataStore ds = getDataStore();
		PrintWriter writer = res.getWriter();
		HtmlWriter.writeHeader(configurationManager, gc, hostname, writer);
		if (!readOnly)
		{
			writer.println("<alert><div class=\"alertbar\">");
			writer.println("<input class=\"version\" type=\"text\" id=\"newVersionAll\" />");
			writer.println("<button class=\"command\" onClick=\"switchVersionToCheckedItems()\">Switch version to selected nodes</button>");
			writer.println("<button class=\"command\" onClick=\"selectAll()\">Select all</button>");
			writer.println("</div></alert>");
		}
		for (Node node : ds.appInstances())
		{
			String version = getVersion(ds, node);
			if (versionFilter.filterByVersion(version))
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
			if (!fail && alerts)
			{
				continue;
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
				String versionInPeerLink = getLink(new VersionCollector(), node);
				line += "<li><a class=\"" + versionClass + "\" href=\"" + versionInPeerLink  + "\">" + version + "</a></li>";
			}
			if (!readOnly)
			{
				if (gc.isSwitchVersionEnabled())
				{
					String link = node.peer.getPeerSwitchVersionLink(node.name, "");
					line += "<li>" + "<input class=\"version\" type=\"text\" id=\"" + node.name + "_newVersion\" />" + "</li>" + "<li>"
							+ "<button class=\"command\" onClick=\"switchVersion('" + node.name + "','" + link + "')\">Switch-Version</button>" + "</li>";
				}
				for (Command command : configurationManager.getConfiguredProject().command)
				{
					String link = node.peer.getPeerCommandLink(node.name, command.name);
					line += "<li>" + "<button class=\"command\" onClick=\"commandNode('" + node.name + "','" + command.title() + "','" + link + "')\">"
							+ command.title() + "</button>" + "</li>";
				}
				line += "<li><input class=\"checkbox\" type=\"checkbox\" id=\"checkbox_" + node.name + "\"/></li>";
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
	private String getVersion(DataStore ds, Node node)
	{
		return VersionResult.getVersionOrNull(ds, node);
	}
	
	private String getLink(HttpCollector collector, Node node)
	{
		return Constants.CLIENT_LINK.replace(Constants.PEER_NAME, node.peer.dnsName()).replace(Constants.NODE_NAME, node.name)
				.replace(Constants.COLLECTOR_NAME, collector.name)
				.replace(Constants.CLIENT_PORT, configurationManager.getCurrentGlobalConfiguration().getClientPort() + "");
	}
	
	private DataStore getDataStore()
	{
		return DataStoreRetriever.getD();
	}
	
}
