package yami;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import yami.configuration.ConfigurationManager;
import yami.configuration.HttpCollector;
import yami.configuration.KeepaliveCollector;
import yami.configuration.Node;
import yami.model.Constants;
import yami.model.Result;

import com.google.common.base.Joiner;
import com.google.inject.Inject;

public class CollectorHttpResultFetcher
{
	private static final Logger log = Logger.getLogger(CollectorHttpResultFetcher.class);
	private ConfigurationManager configurationManager;
	
	
	@Inject
	public CollectorHttpResultFetcher(ConfigurationManager configurationManager)
	{
		super();
		this.configurationManager = configurationManager;
	}

	public Result getResult(HttpCollector c, Node n) throws IOException, InterruptedException
	{
		String resultURL = Constants.CLIENT_LINK;
		resultURL = resultURL.replace(Constants.PEER_NAME, n.peer.dnsName()).replace(Constants.NODE_NAME, n.name).replace(Constants.COLLECTOR_NAME, c.name).replace(Constants.CLIENT_PORT, configurationManager.getCurrentGlobalConfiguration().getPeerPort() + "");
		log.debug("Will try to fetch result from " + resultURL);
		List<String> lines = readHTTP(resultURL);
		String output = Joiner.on("\n").join(lines);
		Result res = new Result(0, output);
		if (c.name.equals(new KeepaliveCollector().name))
		{
			res = getKeepaliveResult(lines, res);
		}
		else if (lines.isEmpty())
		{
			return null;
		}
		else
		{
			int exitstatus = 1;
			for (String line : lines) {
				if (line.startsWith("| exitstatus:")){
					exitstatus = Integer.valueOf(line.split(" ")[2]);
					break;
				}
			}
			res = new Result(exitstatus, output);
		}
		return res;
	}
	
	private Result getKeepaliveResult(List<String> lines, Result res)
	{
		long serverEpoch = System.currentTimeMillis() / 1000;
		
		long clientEpoch = 0;
		if (!lines.isEmpty())
		{
			clientEpoch = Long.parseLong(lines.get(lines.size()-1));
			log.debug("server time - client time :" + serverEpoch + "-" + clientEpoch + "=" + (serverEpoch - clientEpoch));
		}
		if (clientEpoch == 0 || serverEpoch - clientEpoch > TimeUnit.MINUTES.toSeconds(5))
		{
			lines.add(0, "Keepalive failed, node is either down or not updating. Monitor result was:\n");
			String output = Joiner.on("\n").join(lines);
			return new Result(1, output);
		}
		return res;
	}
	
	private List<String> readHTTP(String resultURL)
	{
		List<String> resultLines = new ArrayList<String>();
		URL url;
		BufferedReader in;
		try
		{
			url = new URL(resultURL);
			in = new BufferedReader(new InputStreamReader(url.openStream()));
			
			String line;
			while ((line = in.readLine()) != null)
			{
				resultLines.add(line);
			}
			in.close();
		}
		catch (SocketException e)
		{
			log.info("Could not read page " + resultURL + " " + e.getMessage());
		}
		catch (FileNotFoundException e)
		{
			log.info("Could not read page " + resultURL + " " + e.getMessage());
		}
		catch (Exception e)
		{
			log.warn("got an exception at readHTTPHead", e);
		}
		return resultLines;
	}
}
