package codeine.db.mysql;

import java.util.List;

import org.apache.log4j.Logger;

import codeine.jsons.global.MysqlConfigurationJson;
import codeine.utils.StringUtils;


public class NearestHostSelector {

	private static final Logger log = Logger.getLogger(NearestHostSelector.class);
	public static long DIFF_THRESHOLD = 50;
	private List<MysqlConfigurationJson> list;
	private MysqlConfigurationJson lastSql;
	private IDBConnection dbConnection;

	public NearestHostSelector(List<MysqlConfigurationJson> list, IDBConnection dbConnection) {
		this.list = list;
		this.dbConnection = dbConnection;
	}

	public MysqlConfigurationJson select() {
		long minTime = Long.MAX_VALUE;
		long lastSqlMinTime = Long.MAX_VALUE;
		MysqlConfigurationJson $ = null;
		if (lastSql != null) {
			lastSqlMinTime = check(lastSql);
			if (lastSqlMinTime != Long.MAX_VALUE) {
				$ = lastSql;
			}
		}
		for (MysqlConfigurationJson mysql : list) {
			if (mysql == lastSql) {
				continue;
			}
			long total = check(mysql);
			log.info("ping host " + mysql + " time is " + StringUtils.formatTimePeriod(total));
			if (total < minTime){
				if (lastSqlMinTime - total < DIFF_THRESHOLD) {
					$ = lastSql;
				}
				else {
					minTime = total;
					$ = mysql;
				}
			}
		}
		if ($ == null){
			throw new RuntimeException("no host is reachable: " + list);
		}
		log.info("selected host " + $ + " with time " + StringUtils.formatTimePeriod(minTime));
		lastSql = $;
		return $;
	}

	private long check(MysqlConfigurationJson mysql) {
		long start = System.currentTimeMillis();
		if (!dbConnection.checkConnection(mysql.host(), mysql.port(), mysql.user(), mysql.password())){
			return Long.MAX_VALUE;
		}
		return  System.currentTimeMillis() - start;
	}
}
