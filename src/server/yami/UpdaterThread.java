package yami;

import java.util.concurrent.*;

import org.apache.log4j.*;

import yami.configuration.*;
import yami.mail.*;
import yami.model.*;

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
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
		for (HttpCollector c : d.collectors())
		{
			for (Node n : d.appInstances())
			{
				if (shouldSkipNode(c, n))
				{
					continue;
				}
				if (shouldMail(c, n, d))
				{
					mailSender.sendMailIfNeeded(d, c, n, d.getResult(n, c));
				}
			}
		}
	}

	private boolean shouldMail(HttpCollector c, Node n, DataStore d)
	{
		for (HttpCollector master : c.dependsOn())
		{
			CollectorOnAppState r = d.getResult(n,master);
			if (r == null || !r.state() )
			{
				return false;
			}
		}
		return true;
	}

	private boolean shouldSkipNode(HttpCollector c, Node n)
	{
		for (String exNode : c.excludedNode)
		{
			if (exNode.equals(n.name) || exNode.equals(n.nick) || (null != n.node && exNode.equals(n.node.name)))
			{
				return true;
			}
		}
		for (String incNode : c.includedNode)
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
