package yami;

import java.io.*;
import java.util.concurrent.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.*;
import org.eclipse.jetty.server.*;

import yami.configuration.*;
import yami.model.*;

public class ClientRestartServlet extends HttpServlet
{
	private static final Logger log = Logger.getLogger(ClientRestartServlet.class);
	private static final long serialVersionUID = 1L;
	private Server peerHTTPserver;
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		log.info("ClientRestartServlet started");
		PrintWriter writer = res.getWriter();
		writer.println("Recived restart request");
		ConfigurationManager cm = ConfigurationManager.getInstance();
		try
		{
			GlobalConfiguration gc = cm.getConfFromFile(Constants.getConfPath()).conf; //if exception thrown, conf is bad.
			peerHTTPserver.stop();
			Logger.getRootLogger().removeAllAppenders();
			LogManager.shutdown();
			Thread.sleep(TimeUnit.SECONDS.toMillis(5));
			// fix yshabi - hardcoded strings
			String c = "\"";
			c +=  gc.getJavaPath()+ " ";
			if (System.getProperty("debug") != null)
			{
				c += "-Ddebug=" + System.getProperty("debug"); 
			}
			if (System.getProperty("yami.conf") != null)
			{
				c += "-Dyami.conf=" + System.getProperty("yami.conf") + " "; 
			}
			if (System.getProperty("port") != null)
			{
				c += "-Dport=" + System.getProperty("port") + " "; 
			}
			c += "-cp " + Constants.getInstallDir() + "/bin/yami.jar yami.YamiClientBootstrap";
			c += "\"";
			String[] cmd = {"/usr/bin/nohup",c};
			log.info("restart command: "+ cmd);
			Runtime.getRuntime().exec(cmd);
			System.exit(0);			
		}
		catch (RuntimeException e)
		{
			writer.println("failed to read new configuration from file "+ Constants.getConfPath());
			log.warn("failed to read new configuration from file "+ Constants.getConfPath(), e);
		}
		catch (Exception e)
		{
			log.warn("failed to stop current http server", e);
		}
		
		
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
