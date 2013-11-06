package codeine.executer;

import org.apache.log4j.Logger;

import codeine.utils.StringUtils;

import com.google.common.base.Stopwatch;

public class PeriodicExecuter implements Runnable
{
	private final long sleepTimeMilli;
	private Task task;
	private volatile boolean shouldStop;
	private String taskName;
	private static final Logger log = Logger.getLogger(PeriodicExecuter.class);


	public PeriodicExecuter(long sleepTimeMilli, Task task, String taskName) {
		super();
		this.sleepTimeMilli = sleepTimeMilli;
		this.task = task;
		this.taskName = taskName;
	}
	
	@Override
	public void run()
	{
		log.info("started for task " + taskName);
		while (!shouldStop) {
			Stopwatch s = new Stopwatch().start();
			try {
				exec();
			} catch (Exception e1) {
				log.warn("error executing task " + taskName, e1);
			}
			try {
				s.stop();
				log.info("task " + taskName + " took " + s + " ; going to sleep " + StringUtils.formatTimePeriod(sleepTimeMilli));
				Thread.sleep(sleepTimeMilli);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		log.info("finished for task " + taskName);
	}

	private void exec() {
		log.debug("executing " + taskName);
		task.run();
	}

	public void stopWhenPossible() {
		shouldStop = true;
	}

	public void runInThread() {
		new Thread(this).start();
	}
}
