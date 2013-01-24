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
		resultURL = resultURL.replace(Constants.NODE_NAME, n.peer.name).replace(Constants.APP_NAME, n.name).replace(Constants.COLLECTOR_NAME, c.name);
		log.debug("Will try to fetch result from " + resultURL);
		List<String> lines = readHTTP(resultURL);
		String output = Joiner.on("\n").join(lines);
		Result res = new Result(0, output);
		if (c.name.equals("keepalive"))
		{
			res = getKeepaliveResult(lines, res);
		}
		else if (lines.isEmpty())
		{
			return null;
		}
		else
		{
			res = new Result(lines.get(0).equals("True") ? 0 : 1, output);
		}
		return res;
	}
	
	private Result getKeepaliveResult(List<String> lines, Result res)
	{
		long serverEpoch = System.currentTimeMillis() / 1000;
		
		long clientEpoch = 0;
		if (!lines.isEmpty() && lines.size() >= 4)
		{
			clientEpoch = Long.parseLong(lines.get(3));
			log.debug("server time - client time :" + serverEpoch + "-" + clientEpoch + "=" + (serverEpoch - clientEpoch));
		}
		if (clientEpoch == 0 || serverEpoch - clientEpoch > TimeUnit.MINUTES.toSeconds(5))
		{
			return new Result(1, "Keepalive failed, node is either down or not updating.");
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
		catch (SocketException ignore)
		{
		}
		catch (FileNotFoundException e)
		{
			log.info("Could not read page " + resultURL);
		}
		catch (Exception e)
		{
			log.warn("got an exception at readHTTPHead", e);
		}
		return resultLines;
	}
}
