package codeine.peers_status;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.db.IStatusDatabaseConnector;
import codeine.executer.Task;

public class OldPeersRemove implements Task{

	private static final Logger log = Logger.getLogger(OldPeersRemove.class);
	public static long INTERVAL = TimeUnit.MINUTES.toMillis(1);
	public static int DELETE_TIME_TO_LIVE = (int) TimeUnit.DAYS.toMinutes(1);
	public static int DISC_TIME_TO_LIVE = (int) TimeUnit.HOURS.toMinutes(12);
	
	
	private @Inject IStatusDatabaseConnector statusDatabaseConnector;
	
	@Override
	public void run() {
		log.info("Removed expired peers");
		statusDatabaseConnector.updatePeersStatus(DELETE_TIME_TO_LIVE, DISC_TIME_TO_LIVE);
	}

}
