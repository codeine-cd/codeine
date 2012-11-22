package yami;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class PeriodicExecuter implements Runnable
{
	private final long sleep_time; // = TimeUnit.SECONDS.toMillis(20);
	private Task task;
	private static final Logger log = Logger.getLogger(PeriodicExecuter.class);


	public PeriodicExecuter(long sleep_time, Task task) {
		super();
		this.sleep_time = TimeUnit.SECONDS.toMillis(sleep_time);
		this.task = task;
	}
	
	@Override
	public void run()
	{
		while (true) {
			log.info("Periodic Executer started");
			exec();
			try {
				log.info("Going to sleep " + TimeUnit.MILLISECONDS.toSeconds(sleep_time) + " seconds");
				Thread.sleep(sleep_time);
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
