package codeine.executer;

import codeine.CodeineUncaughtExceptionHandler;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

public class ThreadPoolUtils {

	private static final int CAPACITY = 1000;

	public static ThreadPoolExecutor newThreadPool(int maximumNumOfThreads, String poolName){
		BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(CAPACITY);
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(maximumNumOfThreads, maximumNumOfThreads, 1, TimeUnit.SECONDS , workQueue, createFactory(poolName));
		threadPoolExecutor.allowCoreThreadTimeOut(true);
		return threadPoolExecutor;
	}
	//TODO I think it is better to use this version and eliminate the other one
	//need to check more about allowCoreThreadTimeOut and assert Error id above amount of tasks
	public static ExecutorService newFixedThreadPool(int concurrency, String poolName) {
		return Executors.newFixedThreadPool(concurrency,createFactory(poolName));
	}
	private static ThreadFactory createFactory(String poolName) {
		return new ThreadFactoryBuilder()
                .setUncaughtExceptionHandler(new CodeineUncaughtExceptionHandler())
                .setNameFormat(poolName+"-%d").build();
	}
}
