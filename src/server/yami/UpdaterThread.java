package yami;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import yami.configuration.HttpCollector;
import yami.configuration.Node;
import yami.model.DataStore;
import yami.model.DataStoreRetriever;
import yami.model.Result;

import com.google.common.base.Stopwatch;

public class UpdaterThread implements Runnable
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
		Stopwatch timer = new Stopwatch().start();
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
					if (n.nick().equals("itstl1064") && c.name.equals("lsof"))
					{
						log.debug(d.resultsByNode.get(n));
					}
				}
				catch (Exception e)
				{
					log.warn("Exception in updateResults.", e);
				}
			}
		}
		timer.stop();
		log.info("updateResults cycle time: " + timer.elapsed(TimeUnit.MILLISECONDS) + " " + TimeUnit.MILLISECONDS.name());
		for (HttpCollector c : d.collectors())
		{
			for (Node n : d.appInstances())
			{
				if (shouldSkipNode(c, n))
				{
					continue;
				}
				mailSender.sendMailIfNeeded(d, c, n, d.getResult(n, c));
			}
		}
	}
	
	private boolean shouldSkipNode(HttpCollector c, Node n)
	{
		for (String exNode : c.excludedNodes)
		{
			if (exNode.equals(n.name) || exNode.equals(n.nick) || (null != n.peer && exNode.equals(n.peer.name)))
			{
				return true;
			}
		}
		for (String incNode : c.includedNodes)
		{
			if (incNode.equals("all"))
			{
				return false;
			}
			if (incNode.equals(n.name) || incNode.equals(n.nick))
			{
				return false;
			}
		}
		return true;
	}
}
