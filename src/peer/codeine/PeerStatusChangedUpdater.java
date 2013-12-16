package codeine;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.db.IStatusDatabaseConnector;
import codeine.jsons.global.GlobalConfigurationJson;
import codeine.jsons.peer_status.PeerStatus;
import codeine.utils.ThreadUtils;

import com.google.common.base.Stopwatch;

public class PeerStatusChangedUpdater implements Runnable{

	private long MAX_TIME_BETWEEN_UPDATES_MILLIS = TimeUnit.MINUTES.toMillis(55);
	private long MIN_TIME_BETWEEN_UPDATES_MILLIS = TimeUnit.SECONDS.toMillis(55);
	
	private static final Logger log = Logger.getLogger(PeerStatusChangedUpdater.class);
	private PeerStatus peerStatus;
	private IStatusDatabaseConnector databaseConnector;
	private GlobalConfigurationJson globalConfigurationJson;
	private BlockingQueue<Object> blockingQueue = new LinkedBlockingQueue<>();
	private Random random = new Random();
			
	
	@Inject
	public PeerStatusChangedUpdater(PeerStatus peerStatus, IStatusDatabaseConnector databaseConnector,
			GlobalConfigurationJson globalConfigurationJson) {
		super();
		this.peerStatus = peerStatus;
		this.databaseConnector = databaseConnector;
		this.globalConfigurationJson = globalConfigurationJson;
		if (!globalConfigurationJson.large_deployment()) {
			MIN_TIME_BETWEEN_UPDATES_MILLIS = TimeUnit.SECONDS.toMillis(5);
			MAX_TIME_BETWEEN_UPDATES_MILLIS = TimeUnit.SECONDS.toMillis(55);
		}
	}

	public void pushUpdate() {
		log.debug("pushUpdate()");
		blockingQueue.add(new Object());
	}

	@Override
	public void run() {
		log.info("start updating");
		ThreadUtils.sleep(TimeUnit.SECONDS.toMillis(31 + random.nextInt(30)));
		while (true){
			try {
				pushUpdateNow();
				ThreadUtils.sleep(MIN_TIME_BETWEEN_UPDATES_MILLIS);
				waitForNextUpdate();
			} catch (Exception e) {
				log.warn("got exception", e);
			}
		}
	}

	private void waitForNextUpdate() {
		try {
			Stopwatch s = new Stopwatch().start();
			log.debug("going to wait at most " + MAX_TIME_BETWEEN_UPDATES_MILLIS + "milli");
			blockingQueue.poll(MAX_TIME_BETWEEN_UPDATES_MILLIS, TimeUnit.MILLISECONDS);
			log.debug("waited " + s);
		} catch (InterruptedException e) {
			log.debug("interrupted", e);
		}
	}

	private void pushUpdateNow() {
		log.info("pushing update now");
		blockingQueue.clear();
		databaseConnector.putReplaceStatus(peerStatus.createJson());
	}
	
}
