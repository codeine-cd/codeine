package codeine.utils;

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
}
