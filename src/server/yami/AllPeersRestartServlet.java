package yami;

import static com.google.common.collect.Lists.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import yami.configuration.Peer;
import yami.model.Constants;
import yami.model.DataStore;
import yami.model.DataStoreRetriever;
import yami.model.Result;
import yami.utils.ProcessExecuter;

public class AllPeersRestartServlet extends HttpServlet
{
	private static final Logger log = Logger.getLogger(AllPeersRestartServlet.class);
	private static final long serialVersionUID = 1L;
	private static final long SILENT_PERIOD = TimeUnit.MINUTES.toMillis(5);
	private PrintWriter writer;
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		log.info("ClientRestartServlet started");
		writer = res.getWriter();
		writer.println("Recived restart all peers request");
		writer.flush();
		DataStore datastore = DataStoreRetriever.getD();
		for (Peer peer : datastore.peers())
		{
			log.info("doGet() - restarting " + peer);
			datastore.addSilentPeriod(peer, System.currentTimeMillis() + SILENT_PERIOD);
			List<String> command = newArrayList(Constants.getInstallDir() + "/bin/restartAllPeers");
			command.add(peer.name);
			Result r = ProcessExecuter.execute(command);
			writer.println("===================================================== peer " + peer.name + " ; executing " + command);
			writer.flush();
			writer.println(r.output);
			writer.flush();
		}
		writer.println("finished!");
		log.info("doGet() - restarting finished");
	}
	
}
