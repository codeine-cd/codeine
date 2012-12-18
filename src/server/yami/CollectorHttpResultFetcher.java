package yami;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import yami.configuration.HttpCollector;
import yami.configuration.Node;
import yami.model.Constants;
import yami.model.Result;

import com.google.common.base.Joiner;

public class CollectorHttpResultFetcher
{
	private static final Logger log = Logger.getLogger(CollectorHttpResultFetcher.class);

	public Result getResult(HttpCollector c, Node n) throws IOException, InterruptedException
	{
		String resultURL = Constants.CLIENT_LINK;
		resultURL = resultURL.replace(Constants.NODE_NAME, n.node.name).replace(Constants.APP_NAME, n.name).replace(Constants.COLLECTOR_NAME, c.name);
		log.debug("Will try to fetch result from " + resultURL);
		List<String> lines = readHTTP(resultURL);
		String output = Joiner.on("\n").join(lines);
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
}
