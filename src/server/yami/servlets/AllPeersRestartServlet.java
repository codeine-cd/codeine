package yami.servlets;

import static com.google.common.collect.Lists.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import yami.configuration.Peer;
import yami.model.Constants;
import yami.model.DataStoreRetriever;
import yami.model.Result;
import yami.utils.ProcessExecuter;

public class AllPeersRestartServlet extends HttpServlet
{
	private class PeerRestartThread implements Runnable
	{
		private final List<String> m_command;
		private final Peer m_peer;
		
		private PeerRestartThread(Peer peer, List<String> command)
		{
			m_peer = peer;
			m_command = command;
		}
		
		@Override
		public void run()
		{
			try
			{
				log.info("running worker " + m_command);
				Result result = ProcessExecuter.execute(m_command);
				if (null != result)
				{
					writeResult(m_peer, m_command, result);
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

	private static final Logger log = Logger.getLogger(AllPeersRestartServlet.class);
	private static final long serialVersionUID = 1L;
	private static final long SILENT_PERIOD = TimeUnit.MINUTES.toMillis(5);
	private PrintWriter writer;
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		try
		{
			doRestart(res);
		}
		catch (Exception ex)
		{
			log.warn("error " ,ex);
		}
	}

	private void doRestart(HttpServletResponse res) throws IOException
	{
		log.info("ClientRestartServlet started");
		writer = res.getWriter();
		writeLine("Recived restart all peers request");
		ExecutorService executor = Executors.newFixedThreadPool(10);
		for (Peer peer : DataStoreRetriever.getD().peers())
		{
			restartPeer(peer, executor);
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
		log.info("doGet() - restarting finished");
	}

	private void restartPeer(Peer peer, ExecutorService executor)
	{
		log.info("doGet() - restarting " + peer);
		DataStoreRetriever.getD().addSilentPeriod(peer, System.currentTimeMillis() + SILENT_PERIOD);
		final List<String> command = newArrayList(Constants.getInstallDir() + "/bin/restartAllPeers");
		command.add(peer.name);
		PeerRestartThread worker = new PeerRestartThread(peer, command);
		executor.execute(worker);
	}

	private void writeResult(Peer peer, List<String> command, Result r)
	{
		writeLine(">>> peer " + peer.name + " ; executing " + command);
		writeLine(r.output);
		writeLine("finished with status " + r.exit());
	}

	private synchronized void writeLine(String x)
	{
		writer.println(x);
		writer.flush();
	}
	
}
