package yami;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

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

public class PeersDashboardServlet extends HttpServlet
{
	private static final Logger log = Logger.getLogger(DashboardServlet.class);
	private static final long serialVersionUID = 1L;
	GlobalConfiguration gc;
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		gc = ConfigurationManager.getInstance().getCurrentGlobalConfiguration();
		String hostname = java.net.InetAddress.getLocalHost().getCanonicalHostName();
		log.debug("dashboard request");
		DataStore ds = getDataStore();
		PrintWriter writer = res.getWriter();
		writer.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
		writer.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		writer.println("<head>");
		writer.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
		// writer.println("<meta http-equiv=\"refresh\" content=\"5\" />");
		writer.println("<title>YAMI Dashboard - Peers administation</title>");
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
