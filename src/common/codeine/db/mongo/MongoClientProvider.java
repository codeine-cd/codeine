package codeine.db.mongo;

import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.utils.Asserter;

import com.google.common.collect.Lists;
import com.google.inject.Provider;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

@SuppressWarnings("unused")
public class MongoClientProvider implements Provider<MongoClient>{

	private static final Logger log = Logger.getLogger(MongoClientProvider.class);
	@Inject
	private GlobalConfigurationJsonStore globalConfiguration;

	@Override
	public MongoClient get() {
		List<ServerAddress> addrs = Lists.newArrayList();
//		for (String host : globalConfiguration.get().db_host()) {
//			try {
//				addrs.add(new ServerAddress(host, Constants.DB_PORT));
//			} catch (UnknownHostException e) {
//				log.warn("error with host " + host, e);
//			}
//		}
		Asserter.isFalse(addrs.isEmpty(), "not hosts found for mongo");
		return new MongoClient(addrs);
	}

}
