package yami;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import yami.configuration.Node;
import yami.model.DataStore;
import yami.model.DataStoreRetriever;
import yami.model.Result;
import yami.utils.ProcessExecuter;

public class RestartServlet extends HttpServlet
{
	private static final Logger log = Logger.getLogger(RestartServlet.class);
	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		log.info("got restart request");
		DataStore ds = getDataStore();
		for (Node node : ds.appInstances())
		{
			restart(node);
		}
	}
	
	private void restart(Node node)
	{
		log.info("restarting " + node.name);
		List<String> cmd = new ArrayList<String>();
		cmd.add("/nfs/iil/disks/iec_sws3/dist/workspace/misc/monitoring/server/yamiSsh.pl");
		cmd.add(node.node.name);
		cmd.add("/usr/intel/pkgs/python/2.7.2/bin/python -tt /nfs/iil/disks/iec_sws3/dist/workspace/misc/monitoring/monitoring_agent.py restart " + node.name);
		log.debug("restarting " + cmd);
		try
		{
			Result result = ProcessExecuter.execute(cmd);
			if (result.success())
			{
				log.info(node.name + " restarted succesfully");
			}
			else
			{
				log.info("fail to restart node " + node.name);
				log.info("result " + result);
			}
		}
		catch (InterruptedException ex)
		{
			ex.printStackTrace();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}

	private DataStore getDataStore()
	{
		return DataStoreRetriever.getD();
	}
}
