package yami.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class URLConnectionReader
{
	public static String get(String url)
	{
		String result = "";
		try
		{
			URL yahoo = new URL(url);
			URLConnection yc = yahoo.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null)
			{
				result += inputLine;
			}
			in.close();
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
		return result;
	}
}
