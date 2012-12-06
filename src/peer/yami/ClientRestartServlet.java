package yami;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.*;

import yami.configuration.*;

public class ClientRestartServlet extends HttpServlet
{
	private static final Logger log = Logger.getLogger(ClientRestartServlet.class);
	private static final long serialVersionUID = 1L;
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		ConfigurationManager cm = ConfigurationManager.getInstance();
		Logger.getRootLogger().removeAllAppenders();
		LogManager.shutdown();
		
		log.info("ClientRestartServlet started");
	}
	
	boolean verifyConfiguration(GlobalConfiguration conf)
	{
		return true;
	}
	
}
