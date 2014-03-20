package codeine.executer;

import org.apache.log4j.Logger;

import codeine.utils.StringUtils;
import codeine.utils.ThreadUtils;

import com.google.common.base.Stopwatch;

public class PeriodicExecuter implements Runnable
{
	private final long sleepTimeMilli;
	private Task task;
	private volatile boolean shouldStop;
	private String taskName;
	private boolean sleepFirst;
	private static final Logger log = Logger.getLogger(PeriodicExecuter.class);


	public PeriodicExecuter(long sleepTimeMilli, Task task, String taskName) {
		super();
		this.sleepTimeMilli = sleepTimeMilli;
		this.task = task;
		this.taskName = taskName;
	}
	
	public PeriodicExecuter(long sleepTime, Task task) {
		this(sleepTime, task, task.getClass().getSimpleName());
	}

	@Override
	public void run()
	{
		log.info("started for task " + taskName);
		if (sleepFirst) {
			log.info("going to sleep first for " + StringUtils.formatTimePeriod(sleepTimeMilli));
			ThreadUtils.wait(getSleepObject(), sleepTimeMilli);
		}
		while (!shouldStop) {
			Stopwatch s = new Stopwatch().start();
			try {
				exec();
			} catch (Exception e1) {
				log.warn("error executing task " + taskName, e1);
			}
			s.stop();
			log.info("task " + taskName + " took " + s + " ; going to sleep " + StringUtils.formatTimePeriod(sleepTimeMilli));
			ThreadUtils.wait(getSleepObject(), sleepTimeMilli);
		}
		log.info("finished for task " + taskName);
	}

	protected Object getSleepObject() {
		if (task instanceof NotifiableTask) {
			NotifiableTask task1 = (NotifiableTask) task;
			return task1.getSleepObject();
		}
		return new Object();
	}

	private void exec() {
		log.debug("executing " + taskName);
		task.run();
	}

	public void stopWhenPossible() {
		shouldStop = true;
	}

	public void runInThread() {
		ThreadUtils.createThread(this, taskName).start();
	}
	public void runInThreadSleepFirst() {
		this.sleepFirst = true;
		ThreadUtils.createThread(this, taskName).start();
	}

	public String name() {
		return taskName;
	}
}
