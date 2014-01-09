package codeine.db.mysql;

import java.util.List;

import org.apache.log4j.Logger;

import codeine.jsons.global.MysqlConfigurationJson;
import codeine.utils.StringUtils;


public class NearestHostSelector {

	private static final Logger log = Logger.getLogger(NearestHostSelector.class);
	private List<MysqlConfigurationJson> list;

	public NearestHostSelector(List<MysqlConfigurationJson> list) {
		this.list = list;
	}

	public MysqlConfigurationJson select() {
		long minTime = Long.MAX_VALUE;
		MysqlConfigurationJson $ = null;
		for (MysqlConfigurationJson mysql : list) {
			long start = System.currentTimeMillis();
			if (!DbUtils.checkConnection(mysql.host(), mysql.port(), mysql.user(), mysql.password())){
				continue;
			}
			long total = System.currentTimeMillis() - start;
			if (total < minTime){
				minTime = total;
				$ = mysql;
			}
		}
		if ($ == null){
			throw new RuntimeException("no host is reachable: " + list);
		}
		log.info("selected host " + $ + " with time " + StringUtils.formatTimePeriod(minTime));
		return $;
	}

}
