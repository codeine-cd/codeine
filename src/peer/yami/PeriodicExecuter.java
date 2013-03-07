package yami;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class PeriodicExecuter implements Runnable
{
	private final long sleepTime; // = TimeUnit.SECONDS.toMillis(20);
	private Task task;
	private static final Logger log = Logger.getLogger(PeriodicExecuter.class);


	public PeriodicExecuter(long sleepTime, Task task) {
		super();
		this.sleepTime = TimeUnit.SECONDS.toMillis(sleepTime);
		this.task = task;
	}
	
	@Override
	public void run()
	{
		while (true) {
			log.info("Periodic Executer started");
			exec();
			try {
				log.info("Going to sleep " + TimeUnit.MILLISECONDS.toSeconds(sleepTime) + " seconds");
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void exec() {
		log.info("Executing (updated) " + task);
		task.exec();
	}
}
