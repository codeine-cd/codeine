package yami.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import yami.configuration.ConfigurationManager;
import yami.configuration.GlobalConfiguration;
import yami.model.DataStore;
import yami.model.DataStoreRetriever;

import com.google.common.collect.Lists;

public class AggregateNodesServlet extends HttpServlet
{
	private static final Logger log = Logger.getLogger(AggregateNodesServlet.class);
	private static final long serialVersionUID = 1L;
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		try
		{
			doGetInternal(req, res);
		}
		catch (Exception ex)
		{
			log.warn("error", ex);
		}
	}
	public void doGetInternal(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		log.debug("aggregate request");
		ConfigurationManager cm = ConfigurationManager.getInstance();
		GlobalConfiguration gc = cm.getCurrentGlobalConfiguration();
		String hostname = gc.server_dns_name != null ? gc.server_dns_name : InetAddress.getLocalHost().getCanonicalHostName();
		DataStore ds = getDataStore();
		PrintWriter writer = res.getWriter();
		HtmlWriter.writeHeader(cm, gc, hostname, writer);
		NodeAggregator aggregator = new NodeAggregator();
		Comparator<VersionItem> comparator = new Comparator<VersionItem>()
		{
			@Override
			public int compare(VersionItem o1, VersionItem o2)
			{
				return o1.version().compareTo(o2.version());
			}};
		List<VersionItem> values = Lists.newArrayList(aggregator.aggregate().values());
		Collections.sort(values, comparator);
		for (VersionItem item : values)
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
			    		"<input class=\"version\" type=\"text\" id=\""+version+"_input\" value=\"" +item.count()+"\"></input>" +
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
