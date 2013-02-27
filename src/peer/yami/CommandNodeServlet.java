package yami;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import yami.model.Constants;
import yami.model.Result;
import yami.utils.ProcessExecuter;

import com.google.common.collect.Lists;

public class CommandNodeServlet extends HttpServlet
{
	private static final Logger log = Logger.getLogger(CommandNodeServlet.class);
	private static final long serialVersionUID = 1L;
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		log.info("CommandNodeServlet doGet");
		PrintWriter writer = res.getWriter();
		writer.println("Recived CommandNodeServlet request");
		String nodeName = req.getParameter("node");
		String command = req.getParameter("command");
		String version = req.getParameter("version");
		writer.println("node = " + nodeName);
		writer.println("command = " + command);
		writer.println("version = " + version);
		String dir = Constants.getInstallDir() + Constants.MONITORS_DIR;
		List<String> cmd = Lists.newArrayList(dir + "/" + command);
		if (version != null)
		{
		    cmd.add(version);
		}
		Result result = ProcessExecuter.execute(cmd);
		writer.println("output: ");
		writer.println(result.output);
	}
}
