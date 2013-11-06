package codeine.utils.network;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetUtils {

	public static InetAddress getLocalHost() {
		try {
			return InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	public static String domain(String hostname) {
		String nameWithoutPort = nameWithoutPort(hostname);
		if (!nameWithoutPort.contains(".")){
			return "";
		}
		return nameWithoutPort.substring(nameWithoutPort.indexOf(".")+1);
	}
	
	public static String nameWithoutPort(String hostport){
		String $ = hostport;
		if ($.contains(":")){
			$ = $.substring(0, $.indexOf(":"));
		}
		return $;
	}

}
