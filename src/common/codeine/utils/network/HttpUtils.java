package codeine.utils.network;

import codeine.utils.ExceptionUtils;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public class HttpUtils
{
	private static final Logger log = Logger.getLogger(HttpUtils.class);
	private final static String USER_AGENT = "Mozilla/5.0";
	public static final int READ_TIMEOUT_MILLI = (int) TimeUnit.MINUTES.toMillis(23);
	public static final int SHORT_READ_TIMEOUT_MILLI = (int) TimeUnit.SECONDS.toMillis(8);
	public static final int MEDIUM_READ_TIMEOUT_MILLI = (int) TimeUnit.MINUTES.toMillis(23);
	
	public static String doGET(String url,Map<String,String> headers, int timeoutMilli)
	{
		final StringBuilder $ = new StringBuilder();
		doGET(url, new OutputToStringFunction($), headers, timeoutMilli);
		return $.toString();
	}
	
	public static void doGET(String url, Function<String, Void> function, Map<String,String> headers, int timeoutMilli)
	{
		try
		{
			URL u = new URL(url);
			URLConnection c = u.openConnection();
			c.setReadTimeout(READ_TIMEOUT_MILLI);
			addHeaders(headers, c);
			BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null)
			{
				function.apply(inputLine);
			}
			in.close();
		}
		catch (Exception ex)
		{
			throw ExceptionUtils.asUnchecked(ex);
		}
	}
	
	public static void doPOST(String url, String postData, Function<String, Void> function, Map<String,String> headers) {
		doPostPut(url, postData, function, headers, "POST");
	}

	private static void doPostPut(String url, String postData, Function<String, Void> function,
			Map<String, String> headers, String method) {
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setReadTimeout(READ_TIMEOUT_MILLI);
			//add reuqest header
			con.setRequestMethod(method);
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			addHeaders(headers, con);
 
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postData);
			wr.flush();
			wr.close();
 
			int responseCode = con.getResponseCode();
			log.debug("resopnse code is " + responseCode);
			if (responseCode != 200){
				throw new RuntimeException("bad response " + responseCode + " for request " + url);
			}
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				function.apply(inputLine);
			}
			in.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String doPUT(String url, String postData, Map<String, String> headers) {
		StringBuilder stringBuilder = new StringBuilder();
		doPostPut(url, postData, new OutputToStringFunction(stringBuilder), headers, "PUT");
		return stringBuilder.toString();
	}
	
	public static String doPOST(String url, String postData, Map<String,String> headers) {
		StringBuilder stringBuilder = new StringBuilder();
		doPOST(url, postData, new OutputToStringFunction(stringBuilder), headers);
		return stringBuilder.toString();
	}

	public static String encodeURL(String parameters){
		try {
			return URLEncoder.encode(parameters, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String encodeHTML(String s)
	{
	    StringBuffer out = new StringBuffer();
	    for(int i=0; i<s.length(); i++)
	    {
	        char c = s.charAt(i);
	        if(c > 127 || c=='"' || c=='<' || c=='>')
	        {
	           out.append("&#"+(int)c+";");
	        }
	        else
	        {
	            out.append(c);
	        }
	    }
	    return out.toString();
	}
	
	public static String specialEncode(String value) {
		return value.replace(':', '_').
				replace(' ', '_').
				replace('>', '_').
				replace('<', '_').
				replace('*', '_').
				replace('"', '_').
				replace('|', '_').
				replace('+', '_').
				replace('/', '_').
				replace('?', '_').
				replace('\\', '_');
	}
	public static String decodeURL(String parameters) {
		try {
			return URLDecoder.decode(parameters, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static void addHeaders(Map<String, String> headers, URLConnection c) {
		if (headers != null) {
			for (Entry<String, String> e : headers.entrySet()) {
				c.setRequestProperty(e.getKey(), e.getValue());
			}
		}
	}

	public static String doGET(String url) {
		return doGET(url, Maps.<String, String>newHashMap(), HttpUtils.READ_TIMEOUT_MILLI);
	}
}
