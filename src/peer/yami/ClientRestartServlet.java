package yami;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.*;

import yami.configuration.*;
import yami.model.*;

public class ClientRestartServlet extends HttpServlet
{
	private static final Logger log = Logger.getLogger(ClientRestartServlet.class);
	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		log.info("got client restart request");
		PrintWriter writer = res.getWriter();
		GlobalConfiguration conf = DataStoreRetriever.readGlobalConfiguration();
		writer.println(conf);
		verifyConfiguration(conf);
		writer.close();
	}
	
	boolean verifyConfiguration(GlobalConfiguration conf)
	{
		
		return true;
	}
	
	
}
