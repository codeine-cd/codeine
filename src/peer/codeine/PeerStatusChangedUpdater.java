package codeine;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import codeine.db.IStatusDatabaseConnector;
import codeine.jsons.info.CodeineRuntimeInfo;
import codeine.jsons.peer_status.PeerStatus;
import codeine.utils.ThreadUtils;

import com.google.common.base.Stopwatch;

public class PeerStatusChangedUpdater implements Runnable{

	private static final long MAX_TIME_BETWEEN_UPDATES_MILLIS = TimeUnit.MINUTES.toMillis(55);
	private static final long MIN_TIME_BETWEEN_UPDATES_MILLIS = TimeUnit.SECONDS.toMillis(55);
	
	private static final Logger log = Logger.getLogger(PeerStatusChangedUpdater.class);
	@Inject private PeerStatus peerStatus;
	@Inject private IStatusDatabaseConnector databaseConnector;
	@Inject private CodeineRuntimeInfo codeineRuntimeInfo;
	
	private BlockingQueue<Object> blockingQueue = new LinkedBlockingQueue<>(); 
			
	public PeerStatusChangedUpdater() {
		super();
		log.setLevel(Level.DEBUG);
	}

	public void pushUpdate() {
		log.debug("pushUpdate()");
		blockingQueue.add(new Object());
	}

	@Override
	public void run() {
		log.info("start updating");
		while (true){
			try {
				ThreadUtils.sleep(MIN_TIME_BETWEEN_UPDATES_MILLIS);
				waitForNextUpdate();
				pushUpdateNow();
			} catch (Exception e) {
				log.warn("got exception", e);
			}
		}
	}

	private void waitForNextUpdate() {
		try {
			Stopwatch s = new Stopwatch().start();
			blockingQueue.poll(MAX_TIME_BETWEEN_UPDATES_MILLIS, TimeUnit.MILLISECONDS);
			log.debug("waited " + s);
		} catch (InterruptedException e) {
			log.debug("interrupted", e);
		}
		blockingQueue.clear();
	}

	private void pushUpdateNow() {
		log.info("pushing update now");
		databaseConnector.putReplaceStatus(peerStatus.createJson());
	}
	
}
