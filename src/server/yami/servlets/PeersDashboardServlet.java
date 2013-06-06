package yami.servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import yami.configuration.ConfigurationManager;
import yami.configuration.GlobalConfiguration;
import yami.configuration.Peer;
import yami.model.Constants;
import yami.model.DataStore;
import yami.model.DataStoreRetriever;

import com.google.inject.Inject;

public class PeersDashboardServlet extends HttpServlet
{
	private static final Logger log = Logger.getLogger(DashboardServlet.class);
	private static final long serialVersionUID = 1L;
	private GlobalConfiguration gc;
	private ConfigurationManager configurationManager;
	
	
	@Inject
	public PeersDashboardServlet(ConfigurationManager configurationManager)
	{
		super();
		this.configurationManager = configurationManager;
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		gc = configurationManager.getCurrentGlobalConfiguration();
		String hostname = gc.server_dns_name != null ? gc.server_dns_name : InetAddress.getLocalHost().getCanonicalHostName();
		log.debug("dashboard request");
		DataStore ds = getDataStore();
		PrintWriter writer = res.getWriter();
		HtmlWriter.writeHeader(configurationManager, gc, hostname, writer);
		if (new File(Constants.getInstallDir() + "/bin/restartAllPeers").canExecute())
		{
			writer.println("    <a class=\"" + "restartbutton" + "\" title=\"" + "Restart All Peers" + "\" href=\"http://" + hostname + ":" + gc.getServerPort() + Constants.RESTART_ALL_PEERS_CONTEXT + "\">Restart All Peers</a><br/>");
		}
		for (Peer peer : ds.peers())
		{
			String line = "            <alert><div class=\"alertbar\"><ul>";
			line += "<li><a class=\"" + "nameg" + "\" href=\"" + peer.getPeerLink() + "\">" + peer.name + "</a></li>";
			line += "<li><a class=\"" + "restartbutton" + "\" title=\"" + "Restart" + "\" href=\"" + peer.getPeerRestartLink() + "\">restart</a></li>";
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
	
	private DataStore getDataStore()
	{
		return DataStoreRetriever.getD();
	}
}
