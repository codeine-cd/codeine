package yami;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import yami.configuration.HttpCollector;
import yami.configuration.KeepaliveCollector;
import yami.configuration.Node;
import yami.model.DataStore;
import yami.model.DataStoreRetriever;
import yami.model.Result;

import com.google.common.base.Stopwatch;

public class UpdaterThread implements Runnable
{
	
	private static final long SLEEP_TIME = TimeUnit.SECONDS.toMillis(10);
	private static final Logger log =
			Logger.getLogger(UpdaterThread.class);
	private final boolean shouldSkipFirst;
	static final String DASHBOARD_URL = "http://";
	private final YamiMailSender mailSender;
	private final CollectorHttpResultFetcher fetcher;
	static boolean isFirstIteration = true;
	
	public UpdaterThread(YamiMailSender yamiMailSender, CollectorHttpResultFetcher fetcher, boolean shouldSkipFirst)
	{
		mailSender = yamiMailSender;
		this.fetcher = fetcher;
		this.shouldSkipFirst = shouldSkipFirst;
	}
	
	@Override
	public void run()
	{
		log.info("Starting UpdaterThread");
		while (true)
		{
			try
			{
				DataStore d = DataStoreRetriever.getD();
				updateResults(d);
				Thread.sleep(SLEEP_TIME);
			}
			catch (Exception ex)
			{
				log.warn("error", ex);
			}
		}
	}
	
	public void updateResults(DataStore d)
	{
		Stopwatch timer = new Stopwatch().start();
		fetchResultsFromAllCollectors(d);
		timer.stop();
		log.info("updateResults cycle time: " + timer.elapsed(TimeUnit.MILLISECONDS) + " " + TimeUnit.MILLISECONDS.name());
		if (isFirstIteration && shouldSkipFirst)
		{
			isFirstIteration = false;
			return;
		}
		mailResultsForAllCollectors(d);
	}

	private void mailResultsForAllCollectors(DataStore d)
	{
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
		for (Node n : d.enabledInternalNodes())
		{
			mailSender.sendMailIfNeeded(d, new KeepaliveCollector(), n, d.getResult(n, new KeepaliveCollector()));
		}
	}

	private void fetchResultsFromAllCollectors(DataStore d)
	{
		updateResultsForCollectorAndNodes(new KeepaliveCollector(), d, d.internalNodes());
		for (HttpCollector c : d.collectors())
		{
			updateResultForCollector(c, d);
		}
		for (HttpCollector c : d.implicitCollectors())
		{
			updateResultForCollector(c, d);
		}
	}

	private void updateResultForCollector(HttpCollector collector, DataStore d) 
	{
	    updateResultsForCollectorAndNodes(collector, d, d.appInstances());
	}

	private void updateResultsForCollectorAndNodes(final HttpCollector collector, final DataStore d, List<Node> nodes)
	{
		ExecutorService executor = Executors.newFixedThreadPool(10);
		log.info("updateResultsForCollectorAndNodes() - starting");
		for (final Node node : nodes)
	    {
			if (shouldSkipNode(collector, node))
			{
				log.debug("updateResultsForCollectorAndNodes() - skipping " + node + " collector " + collector);
				continue;
			}
			Runnable worker = new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						Result r = fetcher.getResult(collector, node);
						if (r != null)
						{
							log.debug("adding result " + r.success() + " to node " + node + " for collector " + collector);
							d.addResults(node, collector, r);
						}
						else
						{
							log.warn("no result fetched for node " + node + " "+ collector);
						}
					}
					catch (Exception e)
					{
						log.warn("Exception in updateResults.", e);
					}
					
				}
			};
			executor.execute(worker );
	    }
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
		log.info("updateResultsForCollectorAndNodes() - finished");
	}
	
	private boolean shouldSkipNode(HttpCollector c, Node n)
	{
		if (n.disabled())
		{
			return true;
		}
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
