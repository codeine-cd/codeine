package codeine.utils;

import java.util.concurrent.ExecutorService;

import codeine.executer.ThreadPoolUtils;



public class ThreadUtils {

	public static void sleep(long millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public static void wait(Object waitObject, long timeout) {
		try {
			synchronized (waitObject) {
				waitObject.wait(timeout);
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public static Thread createThread(Runnable runnable) {
		return createThread(runnable, runnable.getClass().getSimpleName());
	}

	public static Thread createThread(Runnable runnable, String threadName) {
		return new Thread(runnable, threadName);
	}
	
	public static ExecutorService newFixedThreadPool(int concurrency, String poolName) {
		return ThreadPoolUtils.newFixedThreadPool(concurrency, poolName);
	}
}
