package codeine.executer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolUtils {

	private static final int CAPACITY = 1000;

	public static ThreadPoolExecutor newThreadPool(int maximumNumOfThreads){
		BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(CAPACITY);
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(maximumNumOfThreads, maximumNumOfThreads, 1, TimeUnit.SECONDS , workQueue);
		threadPoolExecutor.allowCoreThreadTimeOut(true);
		return threadPoolExecutor;
	}
}
