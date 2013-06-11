package yami;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import yami.configuration.ConfigurationManager;
import yami.configuration.HttpCollector;
import yami.configuration.KeepaliveCollector;
import yami.configuration.Node;
import yami.model.IDataStore;
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
	private IDataStore dataStore;
	private ConfigurationManager configurationManager;
	static boolean isFirstIteration = true;
	
	@Inject
	public UpdaterThread(YamiMailSender yamiMailSender, CollectorHttpResultFetcher fetcher, IDataStore dataStore, ConfigurationManager configurationManager)
	{
		mailSender = yamiMailSender;
		this.fetcher = fetcher;
		this.shouldSkipFirst = true;
		this.dataStore = dataStore;
		this.configurationManager = configurationManager;
	}
	
	@Override
	public void run()
	{
		log.info("Starting UpdaterThread");
		while (true)
		{
			try
			{
				updateResults();
				Thread.sleep(SLEEP_TIME);
			}
			catch (Exception ex)
			{
				log.warn("error", ex);
			}
		}
	}
	
	public void updateResults()
	{
		Stopwatch timer = new Stopwatch().start();
		fetchResultsFromAllCollectors();
		timer.stop();
		log.info("updateResults cycle time: " + timer.elapsed(TimeUnit.MILLISECONDS) + " " + TimeUnit.MILLISECONDS.name());
		if (isFirstIteration && shouldSkipFirst)
		{
			isFirstIteration = false;
			return;
		}
		mailResultsForAllCollectors();
	}

	private void mailResultsForAllCollectors()
	{
		for (HttpCollector c : configurationManager.getConfiguredProject().collectors())
		{
			for (Node n : configurationManager.getConfiguredProject().appInstances())
			{
				if (shouldSkipNode(c, n))
				{
					continue;
				}
				mailSender.sendMailIfNeeded(dataStore, c, n, dataStore.getResult(n, c));
			}
		}
		for (Node n : dataStore.enabledInternalNodes())
		{
			mailSender.sendMailIfNeeded(dataStore, new KeepaliveCollector(), n, dataStore.getResult(n, new KeepaliveCollector()));
		}
	}

	private void fetchResultsFromAllCollectors()
	{
		updateResultsForCollectorAndNodes(new KeepaliveCollector(), configurationManager.getConfiguredProject().internalNodes());
		for (HttpCollector c : configurationManager.getConfiguredProject().collectors())
		{
			updateResultForCollector(c);
		}
		for (HttpCollector c : configurationManager.getConfiguredProject().implicitCollectors())
		{
			updateResultForCollector(c);
		}
	}

	private void updateResultForCollector(HttpCollector collector) 
	{
	    updateResultsForCollectorAndNodes(collector, configurationManager.getConfiguredProject().appInstances());
	}

	private void updateResultsForCollectorAndNodes(final HttpCollector collector, List<Node> nodes)
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
							dataStore.addResults(node, collector, r);
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
