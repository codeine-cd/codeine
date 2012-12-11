package yami;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.*;
import org.eclipse.jetty.server.*;

import yami.configuration.*;

public class ClientRestartServlet extends HttpServlet
{
	private static final Logger log = Logger.getLogger(ClientRestartServlet.class);
	private static final long serialVersionUID = 1L;
	private Server peerHTTPserver;
	
	@SuppressWarnings("unused")
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		log.info("ClientRestartServlet started");
		PrintWriter writer = res.getWriter();
		writer.println("Recived restart request");
		new PeerRestartThread(peerHTTPserver,writer);
	}
	
	boolean verifyConfiguration(GlobalConfiguration conf)
	{
		return true;
	}
	
	public void setStoppedObject(Server peerHTTPserver)
	{
		this.peerHTTPserver = peerHTTPserver;
	}
	
}
