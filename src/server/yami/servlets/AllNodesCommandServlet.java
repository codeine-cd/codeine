package yami.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import yami.configuration.Node;
import yami.model.DataStoreRetriever;
import yami.utils.URLConnectionReader;

import com.google.common.base.Splitter;

public class AllNodesCommandServlet extends HttpServlet
{
	private class PeerRestartThread implements Runnable
	{
		private final String m_link;
		
		public PeerRestartThread(String link)
		{
			m_link = link;
		}

		@Override
		public void run()
		{
			try
			{
				log.info("running worker " + m_link);
				String result = URLConnectionReader.get(m_link);
				if (null != result)
				{
					writeLine("executed " + m_link + "\nresult:\n" + result);
				}
				else
				{
					log.warn("result is null");
				}
			}
			catch (Exception ex)
			{
				log.warn("error in restart" , ex);
			}
		}
	}

	private static final Logger log = Logger.getLogger(AllNodesCommandServlet.class);
	private static final long serialVersionUID = 1L;
	private PrintWriter writer;
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		try
		{
			doRestart(req, res);
		}
		catch (Exception ex)
		{
			log.warn("error " ,ex);
		}
	}

	private void doRestart(HttpServletRequest req, HttpServletResponse res) throws IOException
	{
		log.info("AllNodesCommandServlet started");
		writer = res.getWriter();
		String version = req.getParameter("version");
		if (null == version)
		{
			version = "";
		}
		String nodes = req.getParameter("nodes");
		Iterable<String> split = Splitter.on(",").omitEmptyStrings().split(nodes);
		ExecutorService executor = Executors.newFixedThreadPool(10);
		for (String node : split)
		{
			commandNode(node, executor, version);
		}
		writeLine("waiting for threads");
		executor.shutdown();
		while (!executor.isTerminated()) {
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException ex)
			{
				ex.printStackTrace();
			}
		}
		writer.println("finished!");
		log.info("doRestart() - command finished");
	}

	private void commandNode(String nodeName, ExecutorService executor, String version)
	{
		log.info("doGet() - commandNode " + nodeName);
		Node node = DataStoreRetriever.getD().getNodeByName(nodeName);
		String link = node.peer.getPeerSwitchVersionLink(node.name, "") + version;
		PeerRestartThread worker = new PeerRestartThread(link);
		executor.execute(worker);
	}

	private synchronized void writeLine(String x)
	{
		writer.println(x);
		writer.flush();
	}
	
}
