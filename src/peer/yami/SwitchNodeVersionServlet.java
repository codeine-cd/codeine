package yami;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class SwitchNodeVersionServlet extends HttpServlet
{
	private static final Logger log = Logger.getLogger(SwitchNodeVersionServlet.class);
	private static final long serialVersionUID = 1L;
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		log.info("SwitchNodeVersionServlet doGet");
		PrintWriter writer = res.getWriter();
		writer.println("Recived SwitchNodeVersion request");
		String nodeName = req.getParameter("node");
		String version = req.getParameter("version");
		writer.println("node = " + nodeName);
		writer.println("version = " + version);
	}
}
