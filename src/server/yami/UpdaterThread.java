package yami;

import java.util.concurrent.*;

import org.apache.log4j.*;

import yami.configuration.*;
import yami.model.*;

public class UpdaterThread  implements Runnable
{
	
	private static final long SLEEP_TIME = TimeUnit.SECONDS.toMillis(10);
	private static final Logger log = Logger.getLogger(UpdaterThread.class);
	static final String DASHBOARD_URL = "http://";
	private final YamiMailSender mailSender;
	private final CollectorHttpResultFetcher fetcher;
	
	public UpdaterThread(YamiMailSender yamiMailSender, CollectorHttpResultFetcher fetcher)
	{
		mailSender = yamiMailSender;
		this.fetcher = fetcher;
	}

	@Override
	public void run()
	{
		log.info("Starting UpdaterThread");
		while (true)
		{
			DataStore d = DataStoreRetriever.getD();
			log.info("DataStore retrieved (using " + Constants.getConfPath() + ")");
			updateResults(d);
			try
			{
				Thread.sleep(SLEEP_TIME);
			}
			catch (InterruptedException ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public void updateResults(DataStore d)
	{
		for (HttpCollector c : d.collectors())
		{
			for (Node n : d.appInstances())
			{
				try
				{
					if (shouldSkipNode(c, n))
					{
						continue;
					}
					Result r = fetcher.getResult(c, n);
					log.debug("adding result " + r.success() + " to node " + n);
					d.addResults(n, c, r);
					mailSender.sendMailIfNeeded(d, c, n, d.getResult(n, c));
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}
	
	private boolean shouldSkipNode(HttpCollector c, Node n)
	{
		for (String node : c.excludedNode)
		{
			if (node.equals(n.name) || node.equals(n.nick) || node.equals(n.node.name))
			{
				return true;
			}
		}
		for (String node : c.includedNode)
		{
			if (node.equals("all"))
			{
				return false;
			}
			if (node.equals(n.name) || node.equals(n.nick))
			{
				return false;
			}
		}
		
		return true;
	}
	

}
