package yami;

import java.io.*;
import java.net.*;
import java.util.*;
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
	private YamiMailSender mailSender = new YamiMailSender(new SendMailStrategy());
	
	@Override
	public void run()
	{
		log.info("Starting UpdaterThread");
		while (true)
		{
			DataStore d = DataStoreRetriever.getD();
			log.info("DataStore retrieved (using " + Constants.getConfPath() + ")");
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
						Result r = getResult(c, n);
						log.debug("adding result " + r.success() + " to node " + n);
						d.addResults(n, c, r);
						CollectorOnAppState state = d.getResult(n, c);
						mailSender.sendMailIfNeeded(d, c, n, state);
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
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
	
	private Result getResult(HttpCollector c, Node n) throws IOException, InterruptedException
	{
		String resultURL = Constants.CLIENT_LINK;
		resultURL = resultURL.replace(Constants.NODE_NAME, n.node.name).replace(Constants.APP_NAME, n.name).replace(Constants.COLLECTOR_NAME, c.name);
		log.debug("Will try to fetch result from " + resultURL);
		List<String> lines = readHTTP(resultURL);
		String output = listToString(lines,"\n");
		Result res = new Result(0, output);
		if (lines.isEmpty())
		{
			return null;
		}
		else if (c.name.equals("keepalive"))
		{
			long serverEpoch = System.currentTimeMillis() / 1000;
			long clientEpoch = Long.parseLong(lines.get(3));
			log.debug("server time - client time :" + serverEpoch + "-" + clientEpoch);
			if (serverEpoch - clientEpoch > TimeUnit.MINUTES.toSeconds(2))
			{
				res = new Result(1, "");
			}
		}
		else
		{
			res = new Result(lines.get(0).equals("True") ? 0 : 1, output);
		}
		return res;
	}
	
	private List<String> readHTTP(String resultURL)
	{
		List<String> resultLines = new ArrayList<String>();
		URL url;
		try
		{
			url = new URL(resultURL);
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			while ((line = in.readLine()) != null)
			{
				resultLines.add(line);
			}
			in.close();
		}
		catch (SocketException e)
		{
			log.warn("Bad HTTP response at " + resultURL);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.warn("got an exception at readHTTPHead", e);
		}
		
		return resultLines;
	}
	
	public String listToString(List<String> list, String lineDelimiter)
	{
		StringBuilder sb = new StringBuilder();
		if (list == null || list.isEmpty())
		{
			return "";
		}
		String delimiter = null == lineDelimiter ? "" : lineDelimiter;
		for (String line : list)
		{
			sb.append(line);
			sb.append(delimiter);
		}
		sb.delete((sb.length() - delimiter.length()), sb.length());		
		return sb.toString();
	}
}
