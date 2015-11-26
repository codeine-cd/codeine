package codeine.db.mysql;

import codeine.executer.Task;
import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.jsons.global.MysqlConfigurationJson;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

public class NearestMysqlHostSelectorPeer implements Task, MysqlHostSelector{

	public static final long INTERVAL = TimeUnit.HOURS.toMillis(1);
	private static final Logger log = Logger.getLogger(NearestMysqlHostSelectorPeer.class);
	@Inject
	private GlobalConfigurationJsonStore conf;
	@Inject private IDBConnection dbConnection;
	private MysqlConfigurationJson mysqlConf;
    private NearestHostSelector nearestHostSelector;

    public NearestMysqlHostSelectorPeer()
    {
        nearestHostSelector = new NearestHostSelector(new MysqlConnectionsProvider(conf.get().mysql(), dbConnection));
    }

    @Override
	public MysqlConfigurationJson mysql() {
		return mysql(false);
	}

	private MysqlConfigurationJson mysql(boolean forceNew) {
		if (null != mysqlConf && !forceNew){
			return mysqlConf;
		}
		mysqlConf = selectNearestConf();
		return mysqlConf;
	}

	private MysqlConfigurationJson selectNearestConf() {
		log.info("selectNearestConf - starting");
        MysqlConfigurationJson selectedMysql = nearestHostSelector.select();
		log.info("selectNearestConf - selected mysql " + selectedMysql);
		return selectedMysql;
	}

	@Override
	public String toString() {
		return "NearestMysqlHostSelector [mysqlConf=" + mysqlConf + "]";
	}

	@Override
	public void run() {
		log.info("Checking for nearest mysql db");
		mysql(true);
	}
}
