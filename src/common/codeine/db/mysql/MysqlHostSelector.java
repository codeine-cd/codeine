package codeine.db.mysql;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.executer.Task;
import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.jsons.global.MysqlConfigurationJson;
import codeine.utils.network.InetUtils;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class MysqlHostSelector implements Task{

	public static final long INTERVAL = TimeUnit.HOURS.toMillis(1);
	private static final Logger log = Logger.getLogger(MysqlHostSelector.class);
	@Inject
	private GlobalConfigurationJsonStore conf;
	
	private MysqlConfigurationJson mysqlConf; 
	
	public MysqlConfigurationJson mysql() {
		return mysql(false);
	}

	private MysqlConfigurationJson mysql(boolean forceNew) {
		if (null != mysqlConf && !forceNew){
			return mysqlConf;
		}
		mysqlConf = getLocalConfOrNull();
		if (null != mysqlConf) {
			return mysqlConf;
		}
		mysqlConf = selectNearestConf();
		return mysqlConf;
	}

	private MysqlConfigurationJson selectNearestConf() {
		List<Map.Entry<String, Integer>> hosts = Lists.newArrayList(Iterables.transform(conf.get().mysql(), new Function<MysqlConfigurationJson, Map.Entry<String, Integer>>(){
			@Override
			public Map.Entry<String, Integer> apply(MysqlConfigurationJson m){
				return new AbstractMap.SimpleEntry<String,Integer>(m.host(),m.port());
			}
		}));
		final Map.Entry<String, Integer> host = new NearestHostSelector(hosts).select();
		Predicate<MysqlConfigurationJson> predicate = new Predicate<MysqlConfigurationJson>(){
			@Override
			public boolean apply(MysqlConfigurationJson m){
				return m.host().equals(host.getKey()) && m.port().equals(host.getValue());
			}
		};
		return Iterables.find(conf.get().mysql(), predicate);
	}

	private MysqlConfigurationJson getLocalConfOrNull() {
		for (MysqlConfigurationJson mysqlConfigurationJson : conf.get().mysql()) {
			try {
				if (InetAddress.getByName(mysqlConfigurationJson.host()).equals(InetUtils.getLocalHost())){
					return mysqlConfigurationJson;
				}
			} catch (UnknownHostException e) {
				log.warn("host unknown " + e.getMessage());
			}
		}
		return null;
	}
	public MysqlConfigurationJson getLocalConf() {
		MysqlConfigurationJson localConf = getLocalConfOrNull();
		if (null == localConf) {
			throw new RuntimeException("could not find mysql configuration to start with. host is " + InetUtils.getLocalHost() + " and configuration is " + conf.get().mysql());
		}
		return localConf;
	}

	@Override
	public void run() {
		log.info("Checking for nearest mysql db");
		mysql(true);
	}
}
