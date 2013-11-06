package codeine.peers_status;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.db.IStatusDatabaseConnector;
import codeine.executer.Task;

public class OldPeersRemove implements Task{

	private static final Logger log = Logger.getLogger(OldPeersRemove.class);
	public static long INTERVAL = TimeUnit.MINUTES.toMillis(1);
	public static int DELETE_TIME_TO_LIVE = (int) TimeUnit.DAYS.toMinutes(2);
	public static int DISC_TIME_TO_LIVE = (int) TimeUnit.DAYS.toMinutes(1);
	
	
	private @Inject IStatusDatabaseConnector statusDatabaseConnector;
	
	@Override
	public void run() {
		int removed = statusDatabaseConnector.removeExpiredPeers(DELETE_TIME_TO_LIVE);
		log.info("Removed " + removed + " expired peers");
		int updated = statusDatabaseConnector.updatePeerStatusToDisconnected(DISC_TIME_TO_LIVE);
		log.info("Updated " + updated + " peers to Disc state");
	}

}
