package codeine.db.mysql;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import codeine.utils.StringUtils;


public class NearestHostSelector {

	private static final Logger log = Logger.getLogger(NearestHostSelector.class);
	private List<Map.Entry<String, Integer>> hosts;

	public NearestHostSelector(List<Map.Entry<String, Integer>> hosts) {
		this.hosts = hosts;
	}

	public Map.Entry<String, Integer> select() {
		long minTime = Long.MAX_VALUE;
		Map.Entry<String, Integer> $ = null;
		for (Map.Entry<String, Integer> host : hosts) {
			long start = System.currentTimeMillis();
			if (!DbUtils.checkConnection(host.getKey(), host.getValue())){
				continue;
			}
			long total = System.currentTimeMillis() - start;
			if (total < minTime){
				minTime = total;
				$ = host;
			}
		}
		if ($ == null){
			throw new RuntimeException("no host is reachable: " + hosts);
		}
		log.info("selected host " + $ + " with time " + StringUtils.formatTimePeriod(minTime));
		return $;
	}

}
